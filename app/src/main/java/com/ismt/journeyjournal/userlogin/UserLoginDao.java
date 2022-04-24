package com.ismt.journeyjournal.userlogin;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserLoginDao {
    @Query("SELECT * FROM UserLogin WHERE email = :email and password= :password")
    UserLogin getUserLogin(String email, String password);

    @Insert
    void insert (UserLogin userLogin);
}
