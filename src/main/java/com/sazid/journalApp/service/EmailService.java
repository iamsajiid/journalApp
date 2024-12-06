package com.sazid.journalApp.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public ResponseEntity<?> sendMail(JsonNode incomingMailData){
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(incomingMailData.get("reciever").asText());
            mailMessage.setSubject(incomingMailData.get("subject").asText());
            mailMessage.setText(incomingMailData.get("body").asText());
            javaMailSender.send(mailMessage);
            return ResponseEntity.ok("mail sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error occurred in sendMail method: " + e);
        }
    }
}
