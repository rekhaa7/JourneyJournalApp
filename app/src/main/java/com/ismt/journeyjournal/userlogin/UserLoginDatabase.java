package com.ismt.journeyjournal.userlogin;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UserLogin.class}, version = 1, exportSchema = false)
public abstract class UserLoginDatabase extends RoomDatabase {
    public abstract UserLoginDao getUserLoginDao();
}
