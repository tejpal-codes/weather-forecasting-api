package com.example.weatherforecasting.controller;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;
import com.example.weatherforecasting.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherForecastService;

    /**
     * Endpoint to fetch weather info for a given city.
     * Defaults to \"Melbourne\" if city is not provided.
     *
     * Example: GET /v1/weather?city=Melbourne
     */
    @GetMapping
    public ResponseEntity<WeatherResponseDTO> getWeather(@RequestParam(defaultValue = "Melbourne") String city) {
        log.info("Received weather request for city: {}", city);
        WeatherResponseDTO response = weatherForecastService.getWeather(city);
        return ResponseEntity.ok(response);
    }
}
