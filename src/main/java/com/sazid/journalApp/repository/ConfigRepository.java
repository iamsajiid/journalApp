package com.sazid.journalApp.repository;

import com.sazid.journalApp.entities.ConfigJournalApp;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends MongoRepository<ConfigJournalApp, ObjectId> {

}

