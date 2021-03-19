package com.baddragon.PicProcessor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PicProcessor {

    Set<String> links = new HashSet<>();

    public List<Thread> threads = new LinkedList<>();

    public PicProcessor() {

//        Set<Elements> images = new HashSet<>();
        Document doc = null;
        String url = "https://www.furaffinity.net/gallery/foxkin/";

//        ThreadPageHandler pageHandler = new ThreadPageHandler();
//        pageHandler.setUrl("https://pkg.go.dev/");
//        pageHandler.setUrl("https://wallhaven.cc/");

        Pool pool = new Pool();
        pool.pushLink("https://wallhaven.cc/");
        pool.pushLink("https://pkg.go.dev/");

//        pageHandler.start();
       // links = pageHandler.getLinks();

        //while (true);



//        try {
//            doc = Jsoup.connect(url)
//                    .data("query", "Java")
//                    .userAgent("Mozilla")
//                    .cookie("auth", "token")
//                    .timeout(3000)
//                    .get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Elements elements = doc.select("a[href]");
//        for (Element element : elements) {
//            links.add(element.attr("href"));
//        }
//
//        Document document = null;
//        try {
//            for (String link : links) {
//                document = Jsoup.connect(url + link).get();
//                Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Get images from document object.
//
//
//        System.out.println(links);

    }

}
