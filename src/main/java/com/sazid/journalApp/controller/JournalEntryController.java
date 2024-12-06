package com.sazid.journalApp.controller;

import com.sazid.journalApp.entities.JournalEntry;
import com.sazid.journalApp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getAllJournalsForUser(){
        return journalEntryService.getAllJournalsForUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJournalById(@PathVariable ObjectId id){
        return journalEntryService.getJournalById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        try {
            return journalEntryService.createEntry(myEntry);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateEntry(@PathVariable ObjectId id, @RequestBody JournalEntry newEntry){
        return journalEntryService.updateEntry(id, newEntry);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable ObjectId id){
        return journalEntryService.deleteEntry(id);
    }
}
