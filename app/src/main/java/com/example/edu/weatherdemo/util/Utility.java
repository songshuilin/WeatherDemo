package com.example.edu.weatherdemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.example.edu.weatherdemo.db.CoolWeatherDB;
import com.example.edu.weatherdemo.model.City;
import com.example.edu.weatherdemo.model.Country;
import com.example.edu.weatherdemo.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/5/29.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB
    ,String response){
        if (!TextUtils.isEmpty(response)){

            String[]allProvince=response.split(",");
            if (allProvince!=null&&allProvince.length>0){
                for (String p:allProvince){
                    String[]array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(CoolWeatherDB coolWeatherDB
            ,String response,int provindeId){
        if (!TextUtils.isEmpty(response)){

            String[]allCity=response.split(",");
            if (allCity!=null&&allCity.length>0){
                for (String p:allCity){
                    String[]array=p.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityNmae(array[1]);
                    city.setProvinceId(provindeId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountryResponse(CoolWeatherDB coolWeatherDB
            ,String response,int cityId){
        if (!TextUtils.isEmpty(response)){

            String[]allCountry=response.split(",");
            if (allCountry!=null&&allCountry.length>0){
                for (String p:allCountry){
                    String[]array=p.split("\\|");
                    Country country=new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryNmae(array[1]);
                    country.setCityId(cityId);
                    coolWeatherDB.saveCountry(country);
                }
                return true;
            }
        }
        return false;
    }
    /**
     * 解析服务器返回的Json数据
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                    weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中。
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp,
                                       String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }

}
