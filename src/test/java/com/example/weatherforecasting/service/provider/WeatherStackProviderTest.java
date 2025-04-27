package com.example.weatherforecasting.service.provider;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
public class WeatherStackProviderTest {

    private RestTemplate restTemplate;
    private WeatherStackProvider weatherStackProvider;

    @BeforeEach
    public void setup() {
        restTemplate = mock(RestTemplate.class);
        weatherStackProvider = new WeatherStackProvider(restTemplate);

        // Set @Value fields manually for test
        setField(weatherStackProvider, "apiUrl", "http://mocked-url.com");
        setField(weatherStackProvider, "accessKey", "mockedKey");
        setField(weatherStackProvider, "isEnabled", true);
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
        Map<String, Object> current = Map.of(
                "temperature", 26,
                "wind_speed", 15
        );
        Map<String, Object> mockResponse = Map.of("current", current);

        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(mockResponse);

        WeatherResponseDTO response = weatherStackProvider.getWeather("Melbourne");

        assertNotNull(response);
        assertEquals(26.0, response.getTemperatureDegrees());
        assertEquals(15.0, response.getWindSpeed());
        assertFalse(response.isStale());
    }

    @Test
    public void shouldThrowExceptionWhenDisabled() {
        setField(weatherStackProvider, "isEnabled", false);

        assertThrows(IllegalStateException.class, () -> {
            weatherStackProvider.getWeather("Melbourne");
        });
    }

    @Test
    public void shouldFallbackWhenApiFails() {
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("API failure"));

        assertThrows(RuntimeException.class, () -> {
            weatherStackProvider.getWeather("Melbourne");
        });
    }
}
