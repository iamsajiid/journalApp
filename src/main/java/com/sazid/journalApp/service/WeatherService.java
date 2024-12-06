package com.sazid.journalApp.service;

import com.sazid.journalApp.api.response.WeatherResponse;
import com.sazid.journalApp.config.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Value("${weather.api.key}")
    private String apiKey;

//    private static final String API = "https://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    public ResponseEntity<?> getWeather(String city) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String finalAPI = appCache.appCache.get(AppCache.keys.WEATHER_API.toString()).replace("<apiKey>", apiKey).replace("<city>", city);
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
        if (response != null) {
            WeatherResponse responseBody = response.getBody();
            int temperature = responseBody.getCurrent().getTemperature();
            return ResponseEntity.ok("hi " + authentication.getName() + ", the weather is " + temperature);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}