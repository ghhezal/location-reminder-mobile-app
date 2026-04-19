package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton; // Changed from ImageView to match your XML
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    ImageButton btnSettingsBack;
    TextView tvLoggedUser;
    EditText etCurrentPass, etNewPass, etConfirmPass;
    MaterialButton btnUpdatePass, btnLogout;

    String userEmail = ""; // Changed from currentUser

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        btnSettingsBack = findViewById(R.id.btnSettingsBack);
        tvLoggedUser = findViewById(R.id.tvLoggedUser);
        etCurrentPass = findViewById(R.id.etCurrentPass);
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnUpdatePass = findViewById(R.id.btnUpdatePass);
        btnLogout = findViewById(R.id.btnLogout);

        // Get Email from Intent (Passed from HomeActivity)
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = "User"; // Fallback
        }
        tvLoggedUser.setText(userEmail);

        btnSettingsBack.setOnClickListener(v -> finish());

        btnUpdatePass.setOnClickListener(v -> {
            String current = etCurrentPass.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            if (current.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(this);

            // 1. Verify current password using email
            User verifiedUser = db.userDao().login(userEmail, current);

            if (verifiedUser != null) {
                // 2. Update to new password using email
                db.userDao().updatePassword(userEmail, newPass);
                Toast.makeText(this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();

                // Clear fields after success
                etCurrentPass.setText("");
                etNewPass.setText("");
                etConfirmPass.setText("");
            } else {
                Toast.makeText(this, "Incorrect Current Password", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            // Clear the activity stack and return to Login
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}