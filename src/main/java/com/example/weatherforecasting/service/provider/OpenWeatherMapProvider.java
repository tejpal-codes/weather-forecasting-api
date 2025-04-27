package com.example.weatherforecasting.service.provider;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class OpenWeatherMapProvider implements WeatherProvider {

    private final RestTemplate restTemplate;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.enabled:true}")
    private boolean isEnabled;

    @Override
    @CircuitBreaker(name = "openWeatherMapBreaker", fallbackMethod = "fallbackWeather")
    public WeatherResponseDTO getWeather(String city) {
        if (!isEnabled) {
            log.warn("OpenWeatherMap is disabled via config");
            throw new IllegalStateException("OpenWeatherMap provider is disabled via config");
        }

        try {
            String url = String.format("%s?q=%s,AU&appid=%s&units=metric", apiUrl, city, apiKey);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() == null) {
                throw new IllegalStateException("Empty response from OpenWeatherMap");
            }

            Map<String, Object> main = (Map<String, Object>) response.getBody().get("main");
            Map<String, Object> wind = (Map<String, Object>) response.getBody().get("wind");

            double temperature = ((Number) main.getOrDefault("temp", 0.0)).doubleValue();
            double windSpeed = ((Number) wind.getOrDefault("speed", 0.0)).doubleValue();

            log.info("OpenWeatherMap response: temp={}, wind={}", temperature, windSpeed);
            return new WeatherResponseDTO(temperature, windSpeed, false);
        } catch (RestClientException | NullPointerException | ClassCastException e) {
            log.error("OpenWeatherMap provider failed: {}", e.getMessage());
            throw new RuntimeException("OpenWeatherMap API failure", e);
        }
    }

    public WeatherResponseDTO fallbackWeather(String city, Throwable t) {
        log.error("OpenWeatherMapProvider fallback triggered for city '{}': {}", city, t.getMessage());
        throw new RuntimeException("OpenWeatherMap service currently unavailable", t);
    }

    @Override
    public boolean isAvailable() {
        return isEnabled;
    }
}
