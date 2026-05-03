package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton; // Changed from ImageView to match your XML
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {

    ImageButton btnSettingsBack;
    TextView tvLoggedUser;
    EditText etCurrentPass, etNewPass, etConfirmPass;
    MaterialButton btnUpdatePass, btnLogout;
    ImageView ivSettingsProfile, btnChangePicture;

    String userEmail = ""; // Changed from currentUser

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String savedPath = saveImageToInternalStorage(uri);
                    if (savedPath != null) {
                        Glide.with(this).load(new File(savedPath)).circleCrop().into(ivSettingsProfile);
                        updateProfilePicture(savedPath);
                    }
                }
            }
    );

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
        ivSettingsProfile = findViewById(R.id.ivSettingsProfile);
        btnChangePicture = findViewById(R.id.btnChangePicture);

        // Get Email from Intent (Passed from HomeActivity)
        userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = "User"; // Fallback
        }
        tvLoggedUser.setText(userEmail);

        loadUserProfile();

        btnSettingsBack.setOnClickListener(v -> finish());
        btnChangePicture.setOnClickListener(v -> mGetContent.launch("image/*"));
        ivSettingsProfile.setOnClickListener(v -> mGetContent.launch("image/*"));

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

            // 1. Fetch user to verify current password
            User currentUser = db.userDao().checkUser(userEmail);

            if (currentUser != null && PasswordUtils.verifyPassword(current, currentUser.password)) {
                // 2. Hash new password and update
                String hashedNewPass = PasswordUtils.hashPassword(newPass);
                db.userDao().updatePassword(userEmail, hashedNewPass);
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

    private void loadUserProfile() {
        AppDatabase db = AppDatabase.getInstance(this);
        User user = db.userDao().checkUser(userEmail);
        if (user != null && user.profileImage != null && !user.profileImage.isEmpty()) {
            Glide.with(this)
                    .load(new File(user.profileImage))
                    .placeholder(R.drawable.ic_person)
                    .circleCrop()
                    .into(ivSettingsProfile);
        }
    }

    private void updateProfilePicture(String imagePath) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            db.userDao().updateProfileImage(userEmail, imagePath);
            runOnUiThread(() -> {
                Toast.makeText(this, "Profile Picture Updated!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}