package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {


    ImageView btnSettingsBack;
    TextView tvLoggedUser;
    EditText etCurrentPass, etNewPass, etConfirmPass;
    MaterialButton btnUpdatePass, btnLogout;


    String currentUser = "";

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


        currentUser = getIntent().getStringExtra("USER_NAME");
        if (currentUser == null || currentUser.isEmpty()) {
            currentUser = "Amine"; // Fallback just in case
        }
        tvLoggedUser.setText(currentUser);

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


            User verifiedUser = db.userDao().login(currentUser, current);

            if (verifiedUser != null) {
                // The current password is correct, so update it!
                db.userDao().updatePassword(currentUser, newPass);
                Toast.makeText(this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();

                etCurrentPass.setText("");
                etNewPass.setText("");
                etConfirmPass.setText("");
            } else {
                Toast.makeText(this, "Incorrect Current Password", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> {
            // Return to Login and clear the back-stack so they can't press "Back" to get in
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}