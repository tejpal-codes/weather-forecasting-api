package com.example.weatherforecasting.service;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import com.example.weatherforecasting.exception.NoCachedWeatherException;
import com.example.weatherforecasting.exception.WeatherProviderUnavailableException;
import com.example.weatherforecasting.service.cache.WeatherCache;
import com.example.weatherforecasting.service.provider.WeatherProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceImplTest {

    private WeatherProvider primaryProvider;
    private WeatherProvider fallbackProvider;
    private WeatherCache weatherCache;
    private WeatherServiceImpl weatherService;

    @BeforeEach
    void setup() {
        primaryProvider = mock(WeatherProvider.class);
        fallbackProvider = mock(WeatherProvider.class);
        weatherCache = mock(WeatherCache.class);
        weatherService = new WeatherServiceImpl(List.of(primaryProvider, fallbackProvider), weatherCache);
    }

    @Test
    void shouldReturnWeatherFromCacheIfFresh() {
        when(weatherCache.get("Melbourne"))
                .thenReturn(new WeatherResponseDTO(20.0, 6.0, false));

        WeatherResponseDTO response = weatherService.getWeather("Melbourne");

        assertNotNull(response);
        assertEquals(20.0, response.getTemperatureDegrees());
        verifyNoInteractions(primaryProvider);
    }

    @Test
    void shouldFetchFromProviderAndCacheIfNotCached() {
        when(weatherCache.get("Melbourne")).thenReturn(null);
        when(primaryProvider.isAvailable()).thenReturn(true);
        when(primaryProvider.getWeather("Melbourne"))
                .thenReturn(new WeatherResponseDTO(25.0, 10.0, false));

        WeatherResponseDTO response = weatherService.getWeather("Melbourne");

        assertNotNull(response);
        assertEquals(25.0, response.getTemperatureDegrees());
        verify(weatherCache).put(eq("Melbourne"), any(WeatherResponseDTO.class));
    }

    @Test
    void shouldFallbackToNextProviderIfFirstFails() {
        when(weatherCache.get("Melbourne")).thenReturn(null);
        when(primaryProvider.isAvailable()).thenReturn(true);
        when(primaryProvider.getWeather("Melbourne")).thenThrow(new RuntimeException("Primary failure"));
        when(fallbackProvider.isAvailable()).thenReturn(true);
        when(fallbackProvider.getWeather("Melbourne"))
                .thenReturn(new WeatherResponseDTO(23.0, 5.0, false));

        WeatherResponseDTO response = weatherService.getWeather("Melbourne");

        assertNotNull(response);
        assertEquals(23.0, response.getTemperatureDegrees());
    }

    @Test
    void shouldThrowNoCachedWeatherExceptionIfAllDisabled() {
        when(weatherCache.get("Melbourne")).thenReturn(null);
        when(primaryProvider.isAvailable()).thenReturn(false);
        when(fallbackProvider.isAvailable()).thenReturn(false);

        NoCachedWeatherException exception = assertThrows(
                NoCachedWeatherException.class,
                () -> weatherService.getWeather("Melbourne")
        );

        assertTrue(exception.getMessage().contains("Melbourne"));
    }

    @Test
    void shouldThrowWeatherProviderUnavailableExceptionIfAllFailWithoutCache() {
        when(weatherCache.get("Melbourne")).thenReturn(null);
        when(primaryProvider.isAvailable()).thenReturn(true);
        when(primaryProvider.getWeather("Melbourne")).thenThrow(new RuntimeException("Primary fail"));
        when(fallbackProvider.isAvailable()).thenReturn(true);
        when(fallbackProvider.getWeather("Melbourne")).thenThrow(new RuntimeException("Fallback fail"));

        WeatherProviderUnavailableException exception = assertThrows(
                WeatherProviderUnavailableException.class,
                () -> weatherService.getWeather("Melbourne")
        );

        assertTrue(exception.getMessage().contains("Melbourne"));
    }
}
