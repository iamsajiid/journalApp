package com.sazid.journalApp.controller;

import com.sazid.journalApp.config.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/refresh-cache")
public class UtilityController {

    @Autowired
    private AppCache appCache;

    @GetMapping
    public void clearAppCache(){
        appCache.init();
    }
}
