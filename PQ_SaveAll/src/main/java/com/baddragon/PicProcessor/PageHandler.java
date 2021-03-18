package com.baddragon.PicProcessor;

import java.util.Set;

public interface PageHandler {

    void setUrl(String url);

    void run();

    Set<String> getLinks();

}
