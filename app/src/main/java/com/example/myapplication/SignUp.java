package com.example.myapplication;

import android.os.Bundle;
import android.util.Patterns; // Added for email validation
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    EditText email, password, confirmPassword, hintPassword;
    Button registerBtn;
    TextView loginText;

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
            } else {
                AppDatabase db = AppDatabase.getInstance(this);

                // Check if email already exists in the database
                if (db.userDao().checkUser(emailStr) != null) {
                    Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Create user with Email, Password, and Hint
                    db.userDao().registerUser(new User(emailStr, pass, hint));
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        loginText.setOnClickListener(v -> finish());
    }
}