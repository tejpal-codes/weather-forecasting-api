package com.example.weatherforecasting.service.provider;
import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class OpenWeatherMapProviderTest {
    private RestTemplate restTemplate;
    private OpenWeatherMapProvider openWeatherMapProvider;

    @BeforeEach
    public void setup() {
        restTemplate = mock(RestTemplate.class);
        openWeatherMapProvider = new OpenWeatherMapProvider(restTemplate);

        // Set @Value fields manually for testing
        setField(openWeatherMapProvider, "apiUrl", "http://mocked-url.com");
        setField(openWeatherMapProvider, "apiKey", "mockedKey");
        setField(openWeatherMapProvider, "isEnabled", true);
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnWeatherDataSuccessfully() {
        Map<String, Object> main = Map.of("temp", 27.5);
        Map<String, Object> wind = Map.of("speed", 12.3);
        Map<String, Object> body = Map.of("main", main, "wind", wind);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(body));

        WeatherResponseDTO response = openWeatherMapProvider.getWeather("Melbourne");

        assertNotNull(response);
        assertEquals(27.5, response.getTemperatureDegrees());
        assertEquals(12.3, response.getWindSpeed());
        assertFalse(response.isStale());
    }

    @Test
    public void shouldThrowExceptionWhenDisabled() {
        setField(openWeatherMapProvider, "isEnabled", false);

        assertThrows(IllegalStateException.class, () -> {
            openWeatherMapProvider.getWeather("Melbourne");
        });
    }

    @Test
    public void shouldFallbackWhenApiFails() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("API failure"));

        assertThrows(RuntimeException.class, () -> {
            openWeatherMapProvider.getWeather("Melbourne");
        });
    }
}
