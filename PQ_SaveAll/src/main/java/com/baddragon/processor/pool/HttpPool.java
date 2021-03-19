package com.baddragon.processor.pool;

import com.baddragon.processor.document.PageDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpPool implements Pool {

    public static Set<String> visitedLinks = new HashSet<>();
    LinkedList<String> links = new LinkedList<>();

    ExecutorService executorService = Executors.newFixedThreadPool(100);

    private String folderName;

    @Override
    public void pushLink(String url) {
        links.add(url);
        synchronized (links) {
            links.notifyAll();
        }
    }

    public HttpPool() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("settings.properties");
        Integer thredsNum = 2;
        Properties properties = new Properties();
        try {
            properties.load(stream);
            thredsNum = Integer.parseInt(properties.getProperty("pool.threadsnumber"));
            if (thredsNum == null) {
                thredsNum = 2;
            }
            this.folderName = properties.getProperty("pool.savefolder");
            if (folderName != null && !folderName.isEmpty()) {
                if(folderName.endsWith("/")){
                    folderName = folderName + "/";
                }
            } else {
                folderName = "temp/";
            }

            File file = new File(folderName);
            if (!file.exists()) {
                file.mkdir();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int index = 0; index < thredsNum; index++) {
            executorService.execute(() -> startLooking());
        }
    }

    public void startLooking() {
        try {
            while (true) {
                synchronized (links) {
                    if (links.isEmpty()) {
                        links.wait();
                    }
                }

                String last = null;
                synchronized (links) {
                    if (!links.isEmpty()) {
                        last = links.removeLast();
                    }
                }

                if (last == null) {
                    continue;
                }

                visitedLinks.add(last);
                loadImages(last);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadImages(String url) {

        byte[] bytes = PageDocument.getPage(url);

        if (bytes.length == 0) {
            return;
        }

        String imgRegEx = "https?://\\S+(?:jpg|jpeg|png)";
        Pattern pattern = Pattern.compile(imgRegEx);

        Matcher matcher = pattern.matcher(new String(bytes));

        while (matcher.find()) {
            String imageName = matcher.group().substring(
                    matcher.group().lastIndexOf("/")
            );
            System.out.println("IMG FOUND " + imageName + " on " + url);
            writeImage(PageDocument.getPage(matcher.group()), imageName);
        }

        String linkRegEx = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        pattern = Pattern.compile(linkRegEx);
        matcher = pattern.matcher(new String(bytes));

        while (matcher.find()) {
            if (!visitedLinks.contains(matcher.group())) {
                System.out.println("link found: " + matcher.group());
                pushLink(matcher.group());
            }

        }
    }

    protected void writeImage(byte[] bytes, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(
                    new File(this.folderName + fileName)
            );

            fileOut.write(bytes);
            System.out.println("saved image " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
