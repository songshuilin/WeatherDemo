package com.example.edu.weatherdemo.util;

/**
 * Created by Administrator on 2016/5/29.
 */
public interface HttpCallbackLinstner {
    void onFinish(String response);
    void onError(Exception e);
}
