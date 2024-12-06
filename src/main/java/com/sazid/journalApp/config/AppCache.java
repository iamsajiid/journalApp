package com.sazid.journalApp.config;

import com.sazid.journalApp.entities.ConfigJournalApp;
import com.sazid.journalApp.repository.ConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    @Autowired
    private ConfigRepository configRepository;

    public Map<String, String> appCache;

    public enum keys {
        WEATHER_API
    }

    @PostConstruct
    public void init(){
        appCache = new HashMap<>();
        List<ConfigJournalApp> appConfig = configRepository.findAll();
        for(ConfigJournalApp configJournalApp : appConfig){
            appCache.put(configJournalApp.getKey(), configJournalApp.getValue());
        }
    }
}
