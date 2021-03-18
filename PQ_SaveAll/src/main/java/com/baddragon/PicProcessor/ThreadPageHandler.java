package com.baddragon.PicProcessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ThreadPageHandler extends Thread implements PageHandler {

    String url;
    static Set<String> links = new HashSet<>();
    static Set<Element> images = new HashSet<>();

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = doc.select("a[href]");
        for (Element element : elements) {
            ThreadPageHandler.links.add(element.attr("href"));
        }

        //for (String link : links) {

        Elements elemImages = doc.select("img[src~=(?i)\\.(png|jpe?g|gif|svg)]");
        Set<String> img = new HashSet<>();
        ThreadPageHandler.images.addAll(elemImages);
        //}


        //Get images from document object.


        System.out.println(links);
    }

    protected void savePics(Elements imgs){
        if ( imgs == null || imgs.isEmpty()) {
            return;
        }

        for (Element elem: imgs) {
            if (ThreadPageHandler.images.contains(elem)){
                continue;
            }


        }

    }

    @Override
    public Set<String> getLinks() {
        return this.links;
    }
}
