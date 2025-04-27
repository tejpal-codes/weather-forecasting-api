package com.example.weatherforecasting.service.cache;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import com.example.weatherforecasting.dto.response.cache.CachedWeather;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleWeatherCache implements WeatherCache {

    private final Map<String, CachedWeather> cache = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 3000; // 3 seconds

    @Override
    public WeatherResponseDTO get(String city) {
        CachedWeather cached = cache.get(city.toLowerCase());
        if (cached != null && cached.isFresh()) {
            WeatherResponseDTO response = cached.getData();
            response.setStale(false);
            return response;
        }
        return null;
    }

    @Override
    public void put(String city, WeatherResponseDTO data) {
        cache.put(city.toLowerCase(), new CachedWeather(data, System.currentTimeMillis()));
    }
}
