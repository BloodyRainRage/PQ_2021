package com.baddragon.processor.pool;

import com.baddragon.processor.logger.Log;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class JsoupPool implements Pool {

    static LinkedList<String> links = new LinkedList<>();

    ExecutorService executorService = Executors.newFixedThreadPool(100);

    static final String docMonitor = "";
    static final String responseMonitor = "";
    static final Set<String> imageNames = new HashSet<>();

    static final Log log = new Log();

    @Override
    public void pushLink(String url) {
        links.add(url);
        //System.out.println("Current list size: " + links.size());
        synchronized (links) {
            links.notifyAll();
        }
    }

    public JsoupPool() {
        for (int index = 0; index < 8; index++) {
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
                if (last == null || last.isEmpty()) {
                    continue;
                }

                Thread.sleep(1000);
                obtainPage(last);


            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            //log.logger.warning(e.getMessage());
        }
    }

    /**
     * Obtains page. Looks for links and images on it
     *
     * @param url URL of page
     */
    protected void obtainPage(String url) {
        Document doc = null;
        synchronized (docMonitor) {
            try {

                doc = getDocument(url);

            } catch (IOException | IllegalArgumentException e) {
                try {
                    Thread.sleep(1000);
                    doc = getDocument(url);
                } catch (IOException | IllegalArgumentException | InterruptedException ex) {
                    ex.printStackTrace();

                    //log.logger.warning(ex.getMessage());
                    if (ex.toString().contains("Status=429")) {
                        pushLink(url);
                    }
                }
            }
        }

        if (doc == null) {
            return;
        }

        //obtains all .png/.jpg/.jpeg/.gif/.svg images
        String imgRegEx = "img[src~=(?i)\\.(png|jpe?g|gif|svg)]";
        Elements elemImages = doc.select(imgRegEx);
        Set<String> img = new HashSet<>();
        savePics(elemImages, url);

        Elements elements = doc.select("a[href]");
        Set<String> localLinks = new HashSet<>();
        for (Element element : elements) {
            String link = element.attr("href");
            if (link.startsWith("/")) {
                link = url + link;
            } else if (link.startsWith("#")) {
                continue;
            }

            localLinks.add(link);
        }

        for (String link : localLinks) {
            if ((!links.contains(link))) {
                pushLink(link);
            }
        }

    }

    Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).userAgent("Mozilla")
                .timeout(10000)
                .get();
    }


    /**
     * Download all Elements from page
     *
     * @param imgs Elements to be downloaded
     * @param url  URL of page
     */
    protected void savePics(Elements imgs, String url) {
        if (imgs == null || imgs.isEmpty()) {
            return;
        }

        for (Element elem : imgs) {

            String imgSource = elem.attr("src");
            if (imgSource.startsWith("//")) {
                imgSource = "http:" + imgSource;
            } else if (imgSource.startsWith("/")) {
                imgSource = url + imgSource;
            }

            String imgName = elem.attr("src");
            imgName = imgName.substring(imgName.lastIndexOf("/"));

            if (imageNames.contains(imgName)) {
                continue;
            }
            imageNames.add(imgName);
            Connection.Response response;
            try {
                Thread.sleep(100);
                synchronized (responseMonitor) {
                    response = Jsoup.connect(imgSource)
                            .ignoreContentType(true).execute();

                    System.out.println("saving picture: " + imgName + " from :" + imgSource);
                    savePic(imgName, response);
                }
            } catch (IOException | InterruptedException e) {
                try {
                    synchronized (responseMonitor) {
                        Thread.sleep(10000);
                        response = getResponse(imgSource);
                        savePic(imgName, response);
                    }
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
//                    log.logger.warning(ex.getMessage());

                }
            }
        }
    }

    protected Connection.Response getResponse(String imgSource) throws IOException {
        return Jsoup.connect(imgSource).userAgent("Mozilla").timeout(0)
                .ignoreContentType(true).execute();
    }

    protected void savePic(String imgName, Connection.Response response) throws IOException {
        FileOutputStream out = (new FileOutputStream("temp/" + imgName));
        out.write(response.bodyAsBytes());
        out.close();
    }
}
