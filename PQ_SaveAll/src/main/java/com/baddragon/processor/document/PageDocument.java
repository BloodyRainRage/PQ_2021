package com.baddragon.processor.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PageDocument {

    public static byte[] getPage(String url){
        try{
            URLConnection connection = (new URL(url)).openConnection();
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteOutArray = new ByteArrayOutputStream();

            int result = inputStream.read();
            while (result != -1){
                byteOutArray.write((byte) result);
                result = inputStream.read();
            }

            inputStream.close();
            ((HttpURLConnection) connection).disconnect();

            return byteOutArray.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  new byte[0];
    }

}
