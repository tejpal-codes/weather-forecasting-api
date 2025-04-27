package com.example.weatherforecasting.service;

import com.example.weatherforecasting.dto.response.WeatherResponseDTO;

public interface WeatherService {
    WeatherResponseDTO getWeather(String city);

}
