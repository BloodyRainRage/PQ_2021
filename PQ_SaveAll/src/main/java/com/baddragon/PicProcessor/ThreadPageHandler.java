package com.baddragon.PicProcessor;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ThreadPageHandler extends Thread implements PageHandler {

    String url;
    static Set<String> links = new HashSet<>();
    static Set<Element> images = new HashSet<>();
    static Set<String> imageNames = new HashSet<>();


    static final String docMonitor = "";
    static final String responseMonitor = "";

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        Document doc = null;
        synchronized (docMonitor) {
            try {

                doc = getDocument();

            } catch (IOException e) {
                try {
                    sleep(1000);
                    doc = getDocument();

                } catch (InterruptedException | IOException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
        if (doc == null) {
            return;
        }

        Elements elemImages = doc.select("img[src~=(?i)\\.(png|jpe?g|gif|svg)]");
        Set<String> img = new HashSet<>();
        savePics(elemImages);

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

        //for (String link : links) {


        //ThreadPageHandler.images.addAll(elemImages);
        //}


        //Get images from document object.

        for (String link : localLinks) {
            if (!ThreadPageHandler.links.contains(link)) {
                ThreadPageHandler.links.add(link);
                ThreadPageHandler threadPageHandler = new ThreadPageHandler();
                threadPageHandler.setUrl(link);
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                threadPageHandler.start();
            }
        }


        System.out.println(links);
    }

    Document getDocument() throws IOException {
        return Jsoup.connect(url)
                .timeout(10000)
                .get();
    }


    protected void savePics(Elements imgs) {
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

            if(imageNames.contains(imgName)){
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

    @Override
    public Set<String> getLinks() {
        return this.links;
    }
}
