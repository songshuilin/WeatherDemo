package com.example.edu.weatherdemo.model;

/**
 * Created by Administrator on 2016/5/29.
 */
public class Country {
    private int id;
    private  String countryNmae;
    private String countryCode;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryNmae() {
        return countryNmae;
    }

    public void setCountryNmae(String countryNmae) {
        this.countryNmae = countryNmae;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", countryNmae='" + countryNmae + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
