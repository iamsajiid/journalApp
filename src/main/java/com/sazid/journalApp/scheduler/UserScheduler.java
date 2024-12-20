//package com.sazid.journalApp.scheduler;
//
//import com.sazid.journalApp.entities.JournalEntry;
//import com.sazid.journalApp.entities.Sentiment;
//import com.sazid.journalApp.entities.User;
//import com.sazid.journalApp.repository.UserRepositoryImpl;
//import com.sazid.journalApp.service.EmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Component
//public class UserScheduler {
//
//    @Autowired
//    private UserRepositoryImpl userRepositoryImpl;
//
//    @Autowired
//    private EmailService emailService;
//
////    @Scheduled(cron = "0 * * * * *")
//    public ResponseEntity<?> getSaUsersAndSendMail(){
//        try {
//            List<User> users = userRepositoryImpl.findSaUsers();
//            for(User user: users){
//                List<JournalEntry> journalEntries = user.getJournalEntries();
//                List<Sentiment> sentimentList = journalEntries.stream().filter(x -> x.getCreatedAt().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
//                        .map(x -> x.getSentiment())
//                        .collect(Collectors.toList());
//                Map<Sentiment, Integer> sentimentFreq= new HashMap<>();
//                for(Sentiment sentiment: sentimentList){
//                    if(sentiment!=null)
//                        sentimentFreq.put(sentiment, sentimentFreq.getOrDefault(sentiment, 0)+1);
//                }
//                int maxCount = 0;
//                Sentiment mostFreqSentiment = null;
//                for(Map.Entry<Sentiment, Integer> entry : sentimentFreq.entrySet()){
//                    if(maxCount < entry.getValue()){
//                        maxCount = entry.getValue();
//                        mostFreqSentiment = entry.getKey();
//                    }
//                }
//                emailService.sendMail(user.getEmail(), "mood of the week for " + user.getUsername(), mostFreqSentiment.toString());
////                System.out.println("mail sent");
//            }
//            return ResponseEntity.ok("mail sent");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("something went wrong in scheduler: " + e);
//        }
//    }
//}

package com.sazid.journalApp.scheduler;

import com.sazid.journalApp.entities.JournalEntry;
import com.sazid.journalApp.entities.Sentiment;
import com.sazid.journalApp.entities.User;
import com.sazid.journalApp.repository.UserRepositoryImpl;
import com.sazid.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private EmailService emailService;

    private static final LocalDateTime SEVEN_DAYS_AGO = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

//    @Scheduled(cron = "0 * * * * *")
    public ResponseEntity<?> getSaUsersAndSendMail() {
        try {
            List<User> users = userRepositoryImpl.findSaUsers();
            users.forEach(user -> processUser(user));
            return ResponseEntity.ok("Emails are being sent");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in scheduler: " + e.getMessage());
        }
    }

    private void processUser(User user) {
        List<JournalEntry> journalEntries = user.getJournalEntries();

        List<Sentiment> recentSentiments = journalEntries.stream()
                .filter(entry -> entry.getCreatedAt().isAfter(SEVEN_DAYS_AGO))
                .map(JournalEntry::getSentiment)
                .collect(Collectors.toList());

        Sentiment mostFrequentSentiment = getMostFrequentSentiment(recentSentiments);

        if (mostFrequentSentiment != null) {
            sendEmail(user.getEmail(), user.getUsername(), mostFrequentSentiment);
        }
    }

    private Sentiment getMostFrequentSentiment(List<Sentiment> sentiments) {
        Map<Sentiment, Long> sentimentFreq = sentiments.stream()
                .filter(sentiment -> sentiment != null)
                .collect(Collectors.groupingBy(sentiment -> sentiment, Collectors.counting()));

        return sentimentFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Async
    private void sendEmail(String userEmail, String username, Sentiment sentiment) {
        String subject = "Mood of the Week for " + username;
        emailService.sendMail(userEmail, subject, sentiment.toString());
    }
}
