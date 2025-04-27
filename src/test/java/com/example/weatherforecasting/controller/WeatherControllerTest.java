package com.example.weatherforecasting.controller;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import com.example.weatherforecasting.exception.NoCachedWeatherException;
import com.example.weatherforecasting.exception.WeatherProviderUnavailableException;
import com.example.weatherforecasting.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
@Import(WeatherControllerTest.MockConfig.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherService weatherService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public WeatherService weatherService() {
            return mock(WeatherService.class);
        }
    }

    @BeforeEach
    void setup() {
        reset(weatherService);
    }

    @Test
    void shouldReturnWeatherData() throws Exception {
        when(weatherService.getWeather("Melbourne"))
                .thenReturn(new WeatherResponseDTO(25.0, 5.0, false));

        mockMvc.perform(get("/v1/weather?city=Melbourne"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperatureDegrees").value(25.0))
                .andExpect(jsonPath("$.windSpeed").value(5.0))
                .andExpect(jsonPath("$.stale").value(false));
    }

    @Test
    void shouldReturn503WhenNoCachedData() throws Exception {
        when(weatherService.getWeather("Melbourne"))
                .thenThrow(new NoCachedWeatherException("Melbourne"));

        mockMvc.perform(get("/v1/weather?city=Melbourne"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").value("No cached weather data available for city: Melbourne"));
    }

    @Test
    void shouldReturn503WhenProvidersUnavailable() throws Exception {
        when(weatherService.getWeather("Melbourne"))
                .thenThrow(new WeatherProviderUnavailableException("Melbourne"));

        mockMvc.perform(get("/v1/weather?city=Melbourne"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.message").value("Weather providers are currently unavailable for city: Melbourne"));
    }

    @Test
    void shouldReturn500OnGenericException() throws Exception {
        when(weatherService.getWeather("Melbourne"))
                .thenThrow(new RuntimeException("Something went wrong"));

        mockMvc.perform(get("/v1/weather?city=Melbourne"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("An unexpected error occurred")));
    }
}
