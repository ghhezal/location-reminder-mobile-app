package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String email;
    public String password;
    public String passwordHint;

    public User(String email, String password, String passwordHint) {
        this.email = email;
        this.password = password;
        this.passwordHint = passwordHint;
    }
}