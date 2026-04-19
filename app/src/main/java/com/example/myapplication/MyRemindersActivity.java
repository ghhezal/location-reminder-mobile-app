package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;
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
        rvReminders.setLayoutManager(new LinearLayoutManager(this));

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadReminders();
    }

    private void loadReminders() {
        new Thread(() -> {
            List<Reminder> list = userDao.getAllReminders();
            runOnUiThread(() -> {
                ReminderAdapter adapter = new ReminderAdapter(list, this);
                rvReminders.setAdapter(adapter);
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders(); // Refresh list if a reminder was edited
    }
}