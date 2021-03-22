package com.baddragon.processor.pool;

import com.baddragon.processor.document.PageDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpPool implements Pool {

    public static Set<String> visitedLinks = Collections.synchronizedSet(new HashSet<>());
    public static Set<String> imageNames = Collections.synchronizedSet(new HashSet<>());
    BlockingQueue<String> links = new LinkedBlockingQueue<>();

    ExecutorService executorService = Executors.newFixedThreadPool(100);

    private String folderName;

    long minFileSize;

    @Override
    public void pushLink(String url) {
        links.add(url);
        synchronized (links) {
            links.notifyAll();
        }
    }

    public HttpPool() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("settings.properties");
        Integer threadsNum = 2;
        Properties properties = new Properties();
        try {
            properties.load(stream);
            threadsNum = Integer.parseInt(properties.getProperty("pool.threadsnumber"));
            if (threadsNum == null) {
                threadsNum = 2;
            }
            this.folderName = properties.getProperty("pool.savefolder");
            if (folderName != null && !folderName.isEmpty()) {
                if (!folderName.endsWith("/")) {
                    folderName = folderName + "/";
                }
            } else {
                folderName = "temp/";
            }

            this.minFileSize = Long.parseLong(properties.getProperty("pool.filesize"));

            if (minFileSize == 0) {
                minFileSize = 20;
            }

            File file = new File(folderName);
            if (!file.exists()) {
                file.mkdir();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int index = 0; index < threadsNum; index++) {
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
                        last = links.take();
                    }
                }

                if (last == null) {
                    continue;
                }

                visitedLinks.add(last);
                obtainPage(last);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void obtainPage(String url) {

        byte[] bytes = new byte[0];
        try {
            bytes = PageDocument.getPage(url);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            if (e.getMessage().contains("code: 429")) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

                links.add(url);
                return;
            }
        }

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

            try {
                writeImage(PageDocument.getPage(matcher.group()), imageName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                if (e.getMessage().contains("code: 429")) {
                    links.add(matcher.group());
                }
            }
        }

        String linkRegEx = "\\b(?<=(href=\"))[^\"]*?(?=\")";
        pattern = Pattern.compile(linkRegEx);
        matcher = pattern.matcher(new String(bytes));

        while (matcher.find()) {
            synchronized (visitedLinks) {
                if (!visitedLinks.contains(matcher.group())) {
                    System.out.println("link found: " + matcher.group());
                    pushLink(matcher.group());
                } else {
                    System.out.println("duplicate link " + matcher.group());
                }

            }

        }
    }

    protected void writeImage(byte[] bytes, String fileName) {

        if (imageNames.contains(fileName)) {
            return;
        }
        imageNames.add(fileName);

        long fileSize = bytes.length / 1024;
        if (fileSize < minFileSize) {
            return;
        }
        try (
                FileOutputStream fileOut = new FileOutputStream(
                        new File(this.folderName + fileName)
                )
        ) {
            fileOut.write(bytes);
            System.out.println("saved image " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
