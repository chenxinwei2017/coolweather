package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast>forecastList;
    public class Basic {
        @SerializedName("city")
        public String cityName;
        @SerializedName("id")
        public String weatherId;
        public class Update{
            @SerializedName("loc")
            public String updateTime;
        }
        public Update update;
    }
    public class AQI{
        public class AQICity{
            public String aqi;
            public String pm25;
            @SerializedName("qlty")
            public String aqiStr;
        }
        public AQICity city;
    }
    public class Now {
        @SerializedName("tmp")
        public String temperature;
        public class More{
            @SerializedName("txt")
            public String info;
        }
        @SerializedName("cond")
        public More more;
        public class Wind{
            @SerializedName("spd")
            public String windSpd;
        }
        public Wind wind;
    }
    public class Suggestion {
        public class Comfort{
            public String brf;
            @SerializedName("txt")
            public String info;
        }
        public class CarWash{
            public String brf;
            @SerializedName("txt")
            public String info;
        }
        public class Wear{
            public String brf;
            @SerializedName("txt")
            public String info;
        }
        public class Flu{
            public String brf;
            @SerializedName("txt")
            public String info;
        }
        public class Sport{
            public String brf;
            @SerializedName("txt")
            public String info;
        }
        public class Trav{
            public String brf;
            @SerializedName("txt")
            public String info;
        }
        public class Uv{
            public String brf;
            @SerializedName("txt")
            public String info;
        }
        @SerializedName("comf")
        public Comfort comfort;
        @SerializedName("cw")
        public CarWash carWash;
        @SerializedName("drsg")
        public Wear wear;
        public Flu flu;
        public Sport sport;
        public Trav trav;
        public Uv uv;
    }
    public class Forecast {
        public String date;
        public class Temperature{
            public String max;
            public String min;
        }
        public class More{
            @SerializedName("txt_d")
            public String info;
        }
        @SerializedName("tmp")
        public Temperature temperature;
        @SerializedName("cond")
        public More more;
    }
}