package com.sazid.journalApp.service;

import com.sazid.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static String apiKey = "e66a4712f6ee9e41f99bfb6ee5f65e97";
    private static String API = "https://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    public ResponseEntity<?> getWeather(String city){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String finalAPI = API.replace("API_KEY", apiKey).replace("CITY", city);
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
        if(response!=null){
            WeatherResponse responseBody = response.getBody();
            int temperature = responseBody.getCurrent().getTemperature();
            return ResponseEntity.ok("hi " + authentication.getName() + ", the weather is " + temperature);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}
