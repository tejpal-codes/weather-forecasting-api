package com.example.weatherforecasting.service.provider;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;

public interface WeatherProvider {
    /**
     * Fetches weather data for a given city
     * @param city city name (e.g., Melbourne)
     * @return temperature and wind speed
     */
    WeatherResponseDTO getWeather(String city);

    /**
     * Checks if the provider is currently available
     * @return true if available, false if failing
     */
    boolean isAvailable();
}
