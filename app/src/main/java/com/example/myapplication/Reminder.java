package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public double latitude;
    public double longitude;
    public boolean isActive = true;

    public Reminder(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}