package com.example.weatherforecasting.exception;

public class WeatherProviderUnavailableException extends BaseAppException {

    public WeatherProviderUnavailableException(String city) {
        super("Weather providers are currently unavailable for city: " + city);
    }

}
