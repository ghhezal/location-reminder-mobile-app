package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyRemindersActivity extends AppCompatActivity {

    private RecyclerView rvReminders;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reminders);

        userDao = AppDatabase.getInstance(this).userDao();
        rvReminders = findViewById(R.id.rvReminders);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        if (rvReminders != null) {
            rvReminders.setLayoutManager(new LinearLayoutManager(this));
        }

        // Navbar - Location Button
        View btnMyLocation = findViewById(R.id.btnMyLocation);
        if (btnMyLocation != null) {
            btnMyLocation.setOnClickListener(v -> {
                // Open Map since we came from Login
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("USER_EMAIL", getIntent().getStringExtra("USER_EMAIL"));
                startActivity(intent);
            });
        }

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MyRemindersActivity.this, SettingsActivity.class);
            // Get the email that was passed from Login to Home, then pass it to Settings
            String email = getIntent().getStringExtra("USER_EMAIL");
            intent.putExtra("USER_EMAIL", email);
            startActivity(intent);
        });

        // Navbar - Plus Button
        View btnAddReminder = findViewById(R.id.btnAddReminder);
        if (btnAddReminder != null) {
            btnAddReminder.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddReminderActivity.class);
                intent.putExtra("USER_EMAIL", getIntent().getStringExtra("USER_EMAIL"));
                startActivity(intent);
            });
        }

        // Navbar - Home Button
        View btnMyReminders = findViewById(R.id.btnMyReminders);
        if (btnMyReminders != null) {
            btnMyReminders.setOnClickListener(v -> {
                if (rvReminders != null) rvReminders.smoothScrollToPosition(0);
            });
        }

        loadReminders();
    }

    private void loadReminders() {
        new Thread(() -> {
            try {
                List<Reminder> list = userDao.getAllReminders();
                runOnUiThread(() -> {
                    if (rvReminders != null) {
                        ReminderAdapter adapter = new ReminderAdapter(list, this);
                        rvReminders.setAdapter(adapter);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }
}