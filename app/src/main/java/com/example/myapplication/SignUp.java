package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns; // Added for email validation
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class SignUp extends AppCompatActivity {

    EditText email, password, confirmPassword, hintPassword;
    Button registerBtn;
    TextView loginText;
    ImageView ivProfilePicture;
    String selectedImagePath = "";

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    String savedPath = saveImageToInternalStorage(uri);
                    if (savedPath != null) {
                        selectedImagePath = savedPath;
                        Glide.with(this).load(new File(selectedImagePath)).circleCrop().into(ivProfilePicture);
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
        setContentView(R.layout.signup_activity);

        // Bind Views - Ensure these IDs match your signup_activity.xml
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.confirm_password);
        hintPassword = findViewById(R.id.hint_password);
        registerBtn = findViewById(R.id.registerBtn);
        loginText = findViewById(R.id.loginText);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        ivProfilePicture.setOnClickListener(v -> mGetContent.launch("image/*"));

        registerBtn.setOnClickListener(v -> {
            String emailStr = email.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirm = confirmPassword.getText().toString().trim();
            String hint = hintPassword.getText().toString().trim();

            if (emailStr.isEmpty() || pass.isEmpty() || hint.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
            // Check if the email is actually a valid email address
            else if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            }
            else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (pass.length()<8){
                Toast.makeText(this, "Password length should be greater than 8 characters", Toast.LENGTH_SHORT).show();
            }
                else {
                AppDatabase db = AppDatabase.getInstance(this);

                // Check if email already exists in the database
                if (db.userDao().checkUser(emailStr) != null) {
                    Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Create user with Email, Password, Hint, and Profile Image
                    db.userDao().registerUser(new User(emailStr, pass, hint, selectedImagePath));
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        loginText.setOnClickListener(v -> finish());
    }
}