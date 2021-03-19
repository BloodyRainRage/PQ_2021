package com.baddragon;

import com.baddragon.processor.pool.HttpPool;
import com.baddragon.processor.pool.Pool;

public class Main {

    public static void main(String[] args) {

        String url = "https://wallhaven.cc/";

        Pool pool = new HttpPool();
        pool.pushLink(url);

    }

}
