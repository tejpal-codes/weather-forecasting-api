package com.example.weatherforecasting.service.provider;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import com.example.weatherforecasting.service.provider.WeatherProvider;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class WeatherStackProvider implements WeatherProvider {
    private final RestTemplate restTemplate;

    @Value("${weatherstack.api.url}")
    private String apiUrl;

    @Value("${weatherstack.api.key}")
    private String accessKey;

    @Value("${weatherstack.api.enabled:true}")
    private boolean isEnabled;

    private boolean available = true;

    @Override
    @CircuitBreaker(name = "weatherStackBreaker", fallbackMethod = "fallbackWeather")
    public WeatherResponseDTO getWeather(String city) {
        if (!isEnabled) {
            throw new IllegalStateException("WeatherStack is disabled via configuration");
        }

        try {
            String url = String.format("%s?access_key=%s&query=%s", apiUrl, accessKey, city);
            Map response = restTemplate.getForObject(url, Map.class);
            Map<String, Object> current = (Map<String, Object>) response.get("current");

            return new WeatherResponseDTO(
                    ((Number) current.get("temperature")).doubleValue(),
                    ((Number) current.get("wind_speed")).doubleValue(),
                    false
            );
        } catch (Exception e) {
            available = false;
            log.error("WeatherStack failure: {}", e.getMessage());
            throw new RuntimeException("WeatherStack API failed", e);
        }
    }

    public WeatherResponseDTO fallbackWeather(String city, Throwable t) {
        log.error("WeatherStackProvider fallback triggered for city '{}': {}", city, t.getMessage());
        throw new RuntimeException("WeatherStack service currently unavailable", t);
    }

    @Override
    public boolean isAvailable() {
        return isEnabled && available;
    }
}
