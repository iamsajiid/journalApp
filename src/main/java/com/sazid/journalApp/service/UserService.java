package com.sazid.journalApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sazid.journalApp.entities.User;
import com.sazid.journalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return ResponseEntity.ok(allUsers);
    }

    public ResponseEntity<?> createUser(User user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<?> updateUser(User updateUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if(!optionalUser.isPresent()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            User user = optionalUser.get();
            user.setUsername((updateUser.getUsername()!=null && !updateUser.getUsername().equals("") ? updateUser.getUsername() : user.getUsername()));
            user.setPassword((updateUser.getPassword()!=null && !updateUser.getPassword().equals("") ? passwordEncoder.encode(updateUser.getPassword()) : user.getPassword()));
            user.setEmail((updateUser.getEmail()!=null && !updateUser.getEmail().equals("") ? updateUser.getEmail() : user.getEmail()));
            user.setSentimentAnalysis(updateUser.getSentimentAnalysis()!=null ? updateUser.getSentimentAnalysis() : user.getSentimentAnalysis());

            userRepository.save(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<?> createAdmin(User user){
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER", "ADMIN"));
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<?> textToSpeech(JsonNode data){
        final String API = "https://api.elevenlabs.io/v1/text-to-speech/VOICE_ID";
        final String apiKey = "sk_e8c27bc9254f3f5fef2defd5bc7d2c76467ada281695d65f";
        final String finalAPI = API.replace("VOICE_ID", data.get("voice_settings").get("voice_id").asText());

        System.out.println(finalAPI);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Accept", "audio/mpeg");
        httpHeaders.set("xi-api-key", apiKey);
        httpHeaders.set("Content-Type", "application/json");

        HttpEntity<JsonNode> httpEntity = new HttpEntity<>(data, httpHeaders);

        try {
            ResponseEntity<String> response = restTemplate.exchange(API, HttpMethod.POST, httpEntity, String.class);
            if(response!=null)
                return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("something went wrong ----- " + e);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
