package com.sazid.journalApp.service;

import com.sazid.journalApp.entities.JournalEntry;
import com.sazid.journalApp.entities.User;
import com.sazid.journalApp.repository.JournalEntryRepository;
import com.sazid.journalApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    public ResponseEntity<List<JournalEntry>> getAllJournalsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if(!optionalUser.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = optionalUser.get();
        List<JournalEntry> allEntries = user.getJournalEntries();
        if(allEntries!=null && !allEntries.isEmpty()) {
            return ResponseEntity.ok(allEntries);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Transactional
    public ResponseEntity<JournalEntry> createEntry(JournalEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            newEntry.setCreatedBy(username);
            JournalEntry createdEntry = journalEntryRepository.save(newEntry);
            User user = userRepository.findByUsername(username).get();
            user.getJournalEntries().add(createdEntry);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getJournalById(ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
        if(!collect.isEmpty()){
            Optional<JournalEntry> optionalJournalEntry = journalEntryRepository.findById(id);
            return ResponseEntity.ok(optionalJournalEntry.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Transactional
    public ResponseEntity<?> deleteEntry(ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).get();
        try {
            boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(myId));
            if(removed){
                userRepository.save(user);
                journalEntryRepository.deleteById(myId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            log.error("error in deleteEntry", e);
            throw new RuntimeException("error occured while deleting journal entry: ", e);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity<?> updateEntry(ObjectId id, JournalEntry newEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            User user = userRepository.findByUsername(username).get();
            List<JournalEntry> collect = user.getJournalEntries().stream().filter(x -> x.getId().equals(id)).collect(Collectors.toList());
            if(!collect.isEmpty()){
                JournalEntry oldEntry = collect.get(0);
                oldEntry.setTitle((newEntry.getTitle()!= null && !newEntry.getTitle().equals("")) ? newEntry.getTitle() : oldEntry.getTitle());
                oldEntry.setContent((newEntry.getContent()!=null && !newEntry.getContent().equals("")) ? newEntry.getContent() : oldEntry.getContent());
                journalEntryRepository.save(oldEntry);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
