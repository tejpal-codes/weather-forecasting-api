package com.example.weatherforecasting.service;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import com.example.weatherforecasting.exception.NoCachedWeatherException;
import com.example.weatherforecasting.exception.WeatherProviderUnavailableException;
import com.example.weatherforecasting.service.cache.WeatherCache;
import com.example.weatherforecasting.service.provider.WeatherProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final List<WeatherProvider> weatherProviders;
    private final WeatherCache weatherCache;

    @Override
    public WeatherResponseDTO getWeather(String city) {
        WeatherResponseDTO cached = weatherCache.get(city);
        if (cached != null) {
            log.info("Returning fresh cached weather for {}", city);
            return cached;
        }

        boolean allDisabled = true;

        for (WeatherProvider provider : weatherProviders) {
            if (provider.isAvailable()) {
                allDisabled = false;
                try {
                    log.info("Fetching weather from provider: {}", provider.getClass().getSimpleName());
                    WeatherResponseDTO data = provider.getWeather(city);
                    data.setStale(false);
                    weatherCache.put(city, data);
                    return data;
                } catch (Exception e) {
                    log.warn("Provider {} failed: {}", provider.getClass().getSimpleName(), e.getMessage());
                }
            } else {
                log.warn("Provider {} is marked unavailable or disabled.", provider.getClass().getSimpleName());
            }
        }

        if (cached != null) {
            log.warn("Returning stale cached weather for {} due to provider failure.", city);
            cached.setStale(true);
            return cached;
        }

        if (allDisabled) {
            log.error("All providers are disabled and no cached data available for city: {}", city);
            throw new NoCachedWeatherException(city);
        }

        log.error("No cached data and all providers failed for city: {}", city);
        throw new WeatherProviderUnavailableException(city);
    }

    @PostConstruct
    public void logConfiguredProviders() {
        log.info("WeatherService initialized with the following providers:");
        weatherProviders.forEach(p -> log.info(" - {} (available: {})", p.getClass().getSimpleName(), p.isAvailable()));
    }
}
