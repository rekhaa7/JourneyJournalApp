package com.ismt.journeyjournal.journal;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface JournalDao {
    @Query("select * from Journal ORDER BY id desc")
    List<JournalEntities> getAllJournals();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertJournal(JournalEntities entities);

    @Delete
    void deleteJournal(JournalEntities entities);

    @Update
    void updateJournal(JournalEntities entities);



}
