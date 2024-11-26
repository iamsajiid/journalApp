package com.sazid.journalApp.controller;

import com.sazid.journalApp.entities.User;
import com.sazid.journalApp.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("update-user")
    public ResponseEntity<?> updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

}