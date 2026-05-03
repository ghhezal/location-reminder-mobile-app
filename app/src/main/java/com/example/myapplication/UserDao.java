package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void registerUser(User user);


    @Query("SELECT * FROM users WHERE email = :email")
    User checkUser(String email);


    @Insert
    void addReminder(Reminder reminder);

    @Query("SELECT * FROM reminders")
    List<Reminder> getAllReminders();

    @Query("SELECT * FROM reminders WHERE id = :id")
    Reminder getReminderById(int id);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("UPDATE users SET password = :newPassword WHERE email = :email")
    void updatePassword(String email, String newPassword);

    @Update
    void updateReminder(Reminder reminder);

    @Query("SELECT passwordHint FROM users WHERE email = :email")
    String getPasswordHint(String email);

    @Query("UPDATE users SET profileImage = :imagePath WHERE email = :email")
    void updateProfileImage(String email, String imagePath);
}