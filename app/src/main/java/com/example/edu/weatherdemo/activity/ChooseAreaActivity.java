package com.example.edu.weatherdemo.activity;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.edu.weatherdemo.R;
import com.example.edu.weatherdemo.adpter.StringAdapter;
import com.example.edu.weatherdemo.db.CoolWeatherDB;
import com.example.edu.weatherdemo.model.City;
import com.example.edu.weatherdemo.model.Country;
import com.example.edu.weatherdemo.model.Province;
import com.example.edu.weatherdemo.util.HttpCallbackLinstner;
import com.example.edu.weatherdemo.util.HttpUtil;
import com.example.edu.weatherdemo.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private StringAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList = new ArrayList<>();
    /*
    省列表
     */
    private List<Province> provinceList;
    /*
   市列表
   */
    private List<City> cityList;
    /*
   县列表
   */
    private List<Country> countryList;
    /*
    选中的省份
     */
    private Province selectProvince;
    /*
    选中的市份
    */
    private City selectCity;
    /*
    当前选中的级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected",false)){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_title);
        adapter =new StringAdapter(dataList,this);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(position);
                    queryCitys();
                } else if (currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(position);
                    queryCountrys();
                }else if (currentLevel==LEVEL_COUNTRY){
                    String countyCode=countryList.get(position).getCountryCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                   intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();

                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /**
     * 查询全国的省，先在数据库去查，没有查询到在从服务器上去查询
     */
    public void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
       if (provinceList.size()>0){
           dataList.clear();
           for (Province province:provinceList){
               dataList.add(province.getProvinceName());
           }
           adapter.notifyDataSetChanged();
           listView.setSelection(0);
           titleText.setText("中国");
           currentLevel=LEVEL_PROVINCE;
       }else {
           queryFromServer(null,"province");
       }
    }
    /**
     * 查询全国的省内所有的市，先在数据库去查，没有查询到在从服务器上去查询
     */
    public void queryCitys() {
        cityList = coolWeatherDB.loadCitys(selectProvince.getId());
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityNmae());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else {
            queryFromServer(selectProvince.getProvinceCode(),"city");
        }
    }
    /**
     * 查询全国的市内所有的县，先在数据库去查，没有查询到在从服务器上去查询
     */
    public void queryCountrys() {
        countryList = coolWeatherDB.loadCoutrys(selectCity.getId());
        if (countryList.size()>0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryNmae());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectCity.getCityNmae());
            currentLevel=LEVEL_COUNTRY;
        }else {
            queryFromServer(selectCity.getCityCode(),"county");
        }
    }
    /**
     * 根据传入的代号和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackLinstner() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(coolWeatherDB,
                            response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(coolWeatherDB,
                            response, selectProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountryResponse(coolWeatherDB,
                            response, selectCity.getId());
                }
                if (result) {
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCitys();
                            } else if ("county".equals(type)) {
                                queryCountrys();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTRY) {
            queryCitys();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {

            finish();
        }
    }

}
