package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void registerUser(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username")
    User checkUser(String username);


    @Insert
    void addReminder(Reminder reminder);

    @Query("SELECT * FROM reminders")
    List<Reminder> getAllReminders();

    @Query("SELECT * FROM reminders WHERE id = :id")
    Reminder getReminderById(int id);

    @Delete
    void deleteReminder(Reminder reminder);

    @Query("UPDATE users SET password = :newPassword WHERE username = :username")
    void updatePassword(String username, String newPassword);
}