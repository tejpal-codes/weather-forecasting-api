package com.example.weatherforecasting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO returned to clients with weather data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDTO {
    private double temperatureDegrees;
    private double windSpeed;
    private boolean stale;
}
