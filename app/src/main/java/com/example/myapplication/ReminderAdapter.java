package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    private List<Reminder> reminders;
    private Context context;
    private UserDao userDao;

    public ReminderAdapter(List<Reminder> reminders, Context context) {
        this.reminders = reminders;
        this.context = context;
        this.userDao = AppDatabase.getInstance(context).userDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.name.setText(reminder.name);
        holder.desc.setText(reminder.description);

        // Delete Logic
        holder.btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                userDao.deleteReminder(reminder);
                reminders.remove(position);
                ((MyRemindersActivity) context).runOnUiThread(() -> notifyDataSetChanged());
            }).start();
        });

        // Edit Logic (Clicking the card)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditReminderActivity.class);
            intent.putExtra("REMINDER_ID", reminder.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, desc;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvReminderName);
            desc = itemView.findViewById(R.id.tvReminderDesc);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}