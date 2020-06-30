package com.test.caplight.weather_weather.util;

import android.app.Application;

public class VariableApp extends Application {
    private static VariableApp instance = null;
    public static VariableApp getInstance(){
        return instance;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }
}
