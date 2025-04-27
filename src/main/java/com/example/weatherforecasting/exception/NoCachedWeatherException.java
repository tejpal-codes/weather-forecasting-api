package com.example.weatherforecasting.exception;

public class NoCachedWeatherException extends BaseAppException {

    public NoCachedWeatherException(String city) {
        super("No cached weather data available for city: " + city);
    }

}
