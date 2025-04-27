package com.example.weatherforecasting.dto.response.cache;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CachedWeather {
    private final WeatherResponseDTO data;
    private final long timestamp;

    public boolean isFresh() {
        return System.currentTimeMillis() - timestamp < 3000;
    }
}