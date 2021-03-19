package com.baddragon.PicProcessor;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pool {

    static LinkedList<String> links = new LinkedList<>();

    ExecutorService executorService = Executors.newFixedThreadPool(100);

    static final String docMonitor = "";
    static final String responseMonitor = "";
    static final Set<String> imageNames = new HashSet<>();

    public void pushLink(String url) {
        links.add(url);
        links.notifyAll();
    }

    public Pool() {
        for (int index = 0; index < 8; index++){
            executorService.execute(()-> startLooking());
        }
    }


    public void startLooking() {
        while (true) {
            try {
                synchronized (links) {
                    links.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String last;
            synchronized (links) {
                last = links.getLast();
            }

            if (last != null && !last.isEmpty()) {
                getPictures(last);
            }

        }
    }

    protected void getPictures(String url) {
        Document doc = null;
        synchronized (docMonitor) {
            try {
                doc = getDocument(url);
            } catch (IOException e) {
                try {
                    doc = getDocument(url);
                } catch (IOException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        if (doc == null) {
            return;
        }

        Elements elemImages = doc.select("img[src~=(?i)\\.(png|jpe?g|gif|svg)]");
        Set<String> img = new HashSet<>();
        savePics(elemImages, url);

        Elements elements = doc.select("a[href]");
        Set<String> localLinks = new HashSet<>();
        for (Element element : elements) {
            String link = element.attr("href");
            if (link.startsWith("/")) {
                String domain;
                if (url.contains("://")) {
                    domain = url.substring(url.indexOf("://"));
                    domain = domain.substring(0, domain.indexOf("/"));
                }
                link = url + link;
            }
            localLinks.add(link);
        }
    }

    Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .timeout(10000)
                .get();
    }

    protected void savePics(Elements imgs, String url) {
        if (imgs == null || imgs.isEmpty()) {
            return;
        }

        for (Element elem : imgs) {
            if (ThreadPageHandler.images.contains(elem)) {
                continue;
            }
            ThreadPageHandler.images.add(elem);

            String imgSource = elem.attr("src");
            if (imgSource.startsWith("//")) {
//                imgSource = imgSource.replaceFirst("//", "");
                imgSource = "http:" + imgSource;
            } else if (imgSource.startsWith("/")) {
                imgSource = url + imgSource;
            }

            String imgName = elem.attr("src");
            imgName = imgName.substring(imgName.lastIndexOf("/"));

            if (imageNames.contains(imgName)) {
                continue;
            }

            ThreadPageHandler.imageNames.add(imgName);

            try {
                synchronized (responseMonitor) {
                    Connection.Response resultImageResponse = Jsoup.connect(imgSource)
                            .ignoreContentType(true).execute();

                    System.out.println("saving picture: " + imgName);
                    FileOutputStream out = (new FileOutputStream("temp/" + imgName));
                    out.write(resultImageResponse.bodyAsBytes());
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
