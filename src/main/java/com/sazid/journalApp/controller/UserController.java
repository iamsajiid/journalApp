package com.sazid.journalApp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.sazid.journalApp.entities.User;
import com.sazid.journalApp.repository.UserRepositoryImpl;
import com.sazid.journalApp.service.EmailService;
import com.sazid.journalApp.service.UserService;
import com.sazid.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sazid.journalApp.scheduler.UserScheduler;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private EmailService emailService;

//    @Autowired
//    private UserScheduler userScheduler;
//
//    @Autowired
//    private UserRepositoryImpl userRepositoryImpl;

    @PutMapping("update-user")
    public ResponseEntity<?> updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @GetMapping("/{city}")
    public ResponseEntity<?> greeting(@PathVariable String city){
        return weatherService.getWeather(city);
    }

    @PostMapping("/text-to-speech")
    public ResponseEntity<?> textToSpeech(@RequestBody JsonNode data){
        return userService.textToSpeech(data);
    }

    @PostMapping("/send-mail")
    public ResponseEntity<?> sendMail(@RequestBody JsonNode incomingMailData){
        return emailService.sendMailTesting(incomingMailData);
    }
//
//    @GetMapping("/send-mood")
//    public ResponseEntity<?> sendMoodOfTheWeek(){
//        return userScheduler.getSaUsersAndSendMail();
//    }

//    @GetMapping("/sa-users")
//    public List<User> getAllSaUsers(){
//        return userRepositoryImpl.findSaUsers();
//    }

}
