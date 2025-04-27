# Weather Forecasting API 🌦️

A robust Spring Boot service to fetch current weather information for cities (default: Melbourne) with **failover** between WeatherStack and OpenWeatherMap, **caching**, **circuit breakers**, and **exception handling**.

---

## 🚀 Features

- Fetches real-time weather from WeatherStack (primary) and OpenWeatherMap (fallback)
- Unified JSON response: **temperature in Celsius** and **wind speed**
- **Caching** results for 3 seconds to reduce API hits
- **Circuit breaker** protection using Resilience4j
- **Failover** to alternate provider if primary is down
- **Serve stale cache** if both providers are unavailable
- Global **Exception handling** with clean error responses
- Clean, **SOLID-compliant** architecture
- Full **Unit and Integration tests** coverage

---

## 📚 Tech Stack

- Java 17
- Spring Boot 3
- Resilience4j (Circuit Breaker)
- JUnit 5 + Mockito (Testing)
- Maven (build tool)
- Lombok (boilerplate reduction)

---

## 🛆 How to Run Locally

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/weather-forecasting-api.git
   cd weather-forecasting-api
   ```

2. **Configure API keys**

   In `src/main/resources/application.properties`:
   ```
   weatherstack.api.key=your_weatherstack_api_key
   openweathermap.api.key=your_openweathermap_api_key
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the API**

    - Local URL:
      ```bash
      curl "http://localhost:8080/v1/weather?city=Melbourne"
      ```
    - Example Response:
      ```json
      {
        "temperatureDegrees": 22.0,
        "windSpeed": 10.5,
        "stale": false
      }
      ```

---

## 🧪 Running Tests

```bash
mvn test
```

✅ Unit tests for Service, Controller, Providers

✅ Mocked cache, providers, API error scenarios

---

## ⚙️ Configuration (application.properties)

```properties
# Server
spring.application.name=weather-forecasting-api
server.port=8080

# WeatherStack
weatherstack.api.url=http://api.weatherstack.com/current
weatherstack.api.key=your_key
weatherstack.api.enabled=true

# OpenWeatherMap
openweathermap.api.url=http://api.openweathermap.org/data/2.5/weather
openweathermap.api.key=your_key
openweathermap.api.enabled=true

# Resilience4j - Circuit Breaker
resilience4j.circuitbreaker.instances.weatherStackBreaker.slidingWindowSize=5
resilience4j.circuitbreaker.instances.weatherStackBreaker.failureRateThreshold=50
resilience4j.circuitbreaker.instances.weatherStackBreaker.waitDurationInOpenState=30s
```

---

## 🌟 Design Highlights

| Aspect | Approach |
|:-------|:---------|
| Caching | Abstracted via `WeatherCache` interface |
| Failover | Ordered `WeatherProvider` beans |
| Circuit Breaker | Resilience4j annotations |
| Stale data | Served if live providers fail |
| Exception handling | Global exception mapper with clean JSON error |

---

## 💡 Possible Future Improvements

- Add **Dockerfile** to containerize the app
- Add **Swagger/OpenAPI** documentation for API discovery
- Add **Prometheus/Grafana** metrics integration
- Enable **multi-city dynamic support** (currently defaults to Melbourne)

---

