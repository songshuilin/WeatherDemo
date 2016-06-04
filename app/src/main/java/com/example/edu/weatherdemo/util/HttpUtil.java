package com.example.edu.weatherdemo.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/5/29.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackLinstner listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL(address);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder respone=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        respone.append(line);
                    }
                    if (listener!=null){
                        listener.onFinish(respone.toString());
                    }
                } catch (Exception e) {
                    listener.onError(e);
                } finally {
                    if (connection!=null){
                        connection.disconnect();
                        connection=null;
                    }
                }
            }
        }).start();
    }

}
