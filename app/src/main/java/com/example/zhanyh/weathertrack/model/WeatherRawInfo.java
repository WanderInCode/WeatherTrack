package com.example.zhanyh.weathertrack.model;

import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

/**
 * Created by zhanyh on 15-10-19.
 */
public class WeatherRawInfo {

    /**
     * status : OK
     * weather : [{"city_name":"上海","city_id":"CHSH000000","last_update":"2015-10-20T18:57:11+08:00","now":{"text":"多云","code":"4","temperature":"21","feels_like":"22","wind_direction":"北","wind_speed":"0","wind_scale":"0","humidity":"73","visibility":"10.0","pressure":"1013.9","pressure_rising":"稳定","air_quality":null}}]
     */

    public String status;
    public List<WeatherEntity> weather;

    public static class WeatherEntity {
        /**
         * city_name : 上海
         * city_id : CHSH000000
         * last_update : 2015-10-20T18:57:11+08:00
         * now : {"text":"多云","code":"4","temperature":"21","feels_like":"22","wind_direction":"北","wind_speed":"0","wind_scale":"0","humidity":"73","visibility":"10.0","pressure":"1013.9","pressure_rising":"稳定","air_quality":null}
         */

        public String city_name;
        public String city_id;
        public Date last_update;
        public NowEntity now;

        public static class NowEntity {
            /**
             * text : 多云
             * code : 4
             * temperature : 21
             * feels_like : 22
             * wind_direction : 北
             * wind_speed : 0
             * wind_scale : 0
             * humidity : 73
             * visibility : 10.0
             * pressure : 1013.9
             * pressure_rising : 稳定
             * air_quality : null
             */

            public String text;
            public String code;
            public String temperature;
            public String wind_direction;
            public String wind_speed;
            public String wind_scale;
            public String humidity;
            public String visibility;
        }
    }
}
