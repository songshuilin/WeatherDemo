package com.example.edu.weatherdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.edu.weatherdemo.model.City;
import com.example.edu.weatherdemo.model.Country;
import com.example.edu.weatherdemo.model.Province;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/29.
 */
public class CoolWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME="cool_weather";
    /*
    数据库版本
     */
    public static final int VERSION=1;
    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME
        ,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }

    /**
     * 获取CoolWeatherDB的实例
     * @param context
     * @return
     */
    public static CoolWeatherDB getInstance(Context context){
      if (coolWeatherDB==null){
          coolWeatherDB=new CoolWeatherDB(context);
      }
        return coolWeatherDB;
    }

    /**
     * 将Province实例存储到数据库中
     * @param province
     */
    public void saveProvince(Province province){
        if (province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 从数据库中读取所有省信息
     * @return
     */
    public List<Province> loadProvinces(){
        List<Province> list=new ArrayList<>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        while(cursor.moveToNext()){
            Province province=new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(province);
        }
        return list;
    }
    /**
     * 将City的实例存储到数据库中
     */
    public void saveCity(City city){
        ContentValues values=new ContentValues();
        values.put("city_name",city.getCityNmae());
        values.put("city_code",city.getCityCode());
        values.put("province_id",city.getProvinceId());
        db.insert("City",null,values);
    }
    /**
     * 获取数据库中的所有市的信息
     *
     */
    public List<City> loadCitys(int provinceId){
        List<City> list=new ArrayList<>();
        Cursor cursor=db.query("City",null,"province_id=?"
                ,new String[]{provinceId+""},null,null,null);
       while (cursor.moveToNext()){
           City city=new City();
           city.setId(cursor.getInt(cursor.getColumnIndex("id")));
           city.setCityNmae(cursor.getString(cursor.getColumnIndex("city_name")));
           city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
           city.setProvinceId(provinceId);
           list.add(city);
       }
        return list;
    }

    /**
     * 将Country的实例存储到数据库中
     */
    public void saveCountry(Country country){
        ContentValues values=new ContentValues();
        values.put("country_name",country.getCountryNmae());
        values.put("country_code",country.getCountryCode());
        values.put("city_id",country.getCityId());
        db.insert("Country",null,values);
    }
    /**
     * 从数据库中某市的县的所有信息
     */
    public List<Country> loadCoutrys(int cityId){
        List<Country>list=new ArrayList<>();
        Cursor cursor=db.query("Country",null,"city_id=?"
                ,new String[]{cityId+""},null,null,null);
      Log.i("TAG","aaaa:::"+cursor);
        while (cursor.moveToNext()){
            Country country=new Country();
            country.setId(cursor.getInt(cursor.getColumnIndex("id")));
            country.setCityId(cityId);
            country.setCountryNmae(cursor.getString(cursor.getColumnIndex("country_name")));
            country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
            list.add(country);
        }
        return list;
    }
}
