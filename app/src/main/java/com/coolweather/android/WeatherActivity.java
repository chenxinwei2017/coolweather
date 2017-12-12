package com.coolweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.service.AutoUpdateService;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import android.support.v7.widget.PopupMenu;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView windInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView aqiStrText;
    private TextView pm25Text;
    private TextView pm25StrText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView wearText;
    private TextView fluText;
    private TextView sportText;
    private TextView travText;
    private TextView uvText;
    private ImageView bingPicImg;
    public int pm25Int;
    public String pm25Str;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private Button navButton;
    public double update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        windInfoText=(TextView)findViewById(R.id.weather_wind);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        aqiStrText=(TextView)findViewById(R.id.aqiStr_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        pm25StrText=(TextView)findViewById(R.id.pm25Str_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        wearText=(TextView) findViewById(R.id.wear_text);
        fluText=(TextView) findViewById(R.id.flu_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        travText=(TextView) findViewById(R.id.trav_text);
        uvText=(TextView) findViewById(R.id.uv_text);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences
                (this);
        String weatherString=prefs.getString("weather",null);
        final String weatherId;
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            weatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateWeather();
            }
        });
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){Glide.with(this).load(bingPic).into(bingPicImg);
        }else{loadBingPic();}
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        final Button chooseUpdateButton=(Button)findViewById(R.id.choose_update);
        chooseUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(chooseUpdateButton);
            }
        });
    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.choose_update, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.quarter_hour:
                        update=0.25;
                        break;
                    case R.id.half_hour:
                        update=0.5;
                        break;
                    case R.id.one_hour:
                        update=1.0;
                        break;
                    case R.id.two_hour:
                        update=2.0;
                        break;
                    case R.id.four_hour:
                        update=4.0;
                        break;
                    case R.id.eight_hour:
                        update=8.0;
                        break;
                    default:
                        update=8.0;
                }
                return true;
            }
        });
        popupMenu.show();
    }
    public void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid=" + weatherId
                + "&key=0324e4c79f2a4e0fad6f60c5c549516f";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(
                                            WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,
                                    "获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime="上次更新时刻："+weather.basic.update.updateTime.split(" ")[1];
        String degree="当前温度："+weather.now.temperature+"℃";
        String weatherInfo="当前天气："+weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        double spd=Integer.valueOf(weather.now.wind.windSpd);
        String windStr;
        if(spd<1){windStr="无风";}
        else if(spd<6){windStr="软风";}
        else if(spd<12){windStr="轻风";}
        else if(spd<20){windStr="微风";}
        else if(spd<29){windStr="和风";}
        else if(spd<39){windStr="清风";}
        else if(spd<50){windStr="强风";}
        else if(spd<62){windStr="劲风";}
        else if(spd<75){windStr="大风";}
        else if(spd<89){windStr="烈风";}
        else if(spd<103){windStr="狂风";}
        else if(spd<117){windStr="暴风";}
        else if(spd<134){windStr="台风";}
        else if(spd<150){windStr="二级强台风";}
        else if(spd<167){windStr="三级强台风";}
        else if(spd<250){windStr="四级超强台风";}
        else {windStr="五级超强台风";}
        windInfoText.setText("当前风速："+weather.now.wind.windSpd+"公里/小时，"
                +windStr);
        forecastLayout.removeAllViews();
        for (Weather.Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,
                    forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max+"℃到"
                    +forecast.temperature.min+"℃");
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            aqiStrText.setText(weather.aqi.city.aqiStr);
            pm25Int=Integer.valueOf(weather.aqi.city.pm25);
            if(pm25Int<35){pm25Str="优";}
            else if (pm25Int<75){pm25Str="良";}
            else if (pm25Int<115){pm25Str="轻度污染";}
            else if (pm25Int<150){pm25Str="中度污染";}
            else if (pm25Int<250){pm25Str="重度污染";}
            else {pm25Str="严重污染";}
            pm25Text.setText(weather.aqi.city.pm25);
            pm25StrText.setText(pm25Str);
        }
        String comfort="舒适度："+weather.suggestion.comfort.brf+"。"+
                weather.suggestion.comfort.info;
        String carWash="洗车指数："+weather.suggestion.carWash.brf+"。"+
                weather.suggestion.carWash.info;
        String sport="运动建议："+weather.suggestion.sport.brf+"。"+
                weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        wearText.setText("穿衣指数："+weather.suggestion.wear.brf+"。"+
                weather.suggestion.wear.info);
        fluText.setText("感冒指数："+weather.suggestion.flu.brf+"。"+
                weather.suggestion.flu.info);
        sportText.setText(sport);
        travText.setText("旅游指数："+weather.suggestion.trav.brf+"。"+
                weather.suggestion.trav.info);
        uvText.setText("紫外线指数："+weather.suggestion.uv.brf+"。"+
                weather.suggestion.uv.info);
        weatherLayout.setVisibility(View.VISIBLE);
        if(weather!=null&&"ok".equals(weather.status)){
            Intent intent=new Intent(this, AutoUpdateService.class);
            intent.putExtra("ud",update);
            startService(intent);
        }else{
            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {e.printStackTrace();}
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).
                        edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).
                                into(bingPicImg);
                    }
                });
            }
        });
    }
    private void updateWeather(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(
                this);
        String weatherString=prefs.getString("weather",null);
        if (weatherString!=null){
            Weather weather=Utility.handleWeatherResponse(weatherString);
            String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid=" + weatherId
                    + "&key=0324e4c79f2a4e0fad6f60c5c549516f";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws
                        IOException {
                    final String responseText=response.body().string();
                    final Weather weather=Utility.handleWeatherResponse(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weather!=null&&"ok".equals(weather.status)){
                                SharedPreferences.Editor editor=PreferenceManager.
                                        getDefaultSharedPreferences(
                                                WeatherActivity.this).edit();
                                editor.putString("weather",responseText);
                                editor.apply();
                                showWeatherInfo(weather);
                            }else {
                                Toast.makeText(WeatherActivity.this,
                                        "获取天气信息失败",Toast.LENGTH_SHORT)
                                        .show();
                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(WeatherActivity.this,
                                    "获取天气信息失败",Toast.LENGTH_SHORT).show();
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }
}
