package com.example.weatherforecasting.service.cache;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;

public interface WeatherCache {
    WeatherResponseDTO get(String city);
    void put(String city, WeatherResponseDTO data);
}
