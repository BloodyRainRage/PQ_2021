package com.baddragon.processor.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PageDocument {

    public static byte[] getPage(String url) throws IOException {
        URLConnection connection = null;
        ByteArrayOutputStream byteOutArray = null;
        try {
            connection = (new URL(url)).openConnection();
            connection.connect();
            try (
                    InputStream inputStream = connection.getInputStream()
            ) {
                byteOutArray = new ByteArrayOutputStream();

                int result = inputStream.read();
                while (result != -1) {
                    byteOutArray.write((byte) result);
                    result = inputStream.read();
                }
            }

            return byteOutArray.toByteArray();
        } finally {
            if (connection != null) {
                ((HttpURLConnection) connection).disconnect();
            }
        }
//        return new byte[0];
    }

}
