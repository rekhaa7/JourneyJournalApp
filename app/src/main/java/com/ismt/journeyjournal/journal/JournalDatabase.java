package com.ismt.journeyjournal.journal;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = JournalEntities.class, exportSchema = false, version = 1)
public abstract class JournalDatabase extends RoomDatabase {

    public static JournalDatabase journalDatabase;
    public static synchronized JournalDatabase getJournalDatabase (Context context){
        if (journalDatabase == null){
            journalDatabase = Room.databaseBuilder(
                    context,
                    JournalDatabase.class,
                    "journal_db"
            ).allowMainThreadQueries().build();
        }
        return journalDatabase;
    }
    public abstract JournalDao journalDao();
}