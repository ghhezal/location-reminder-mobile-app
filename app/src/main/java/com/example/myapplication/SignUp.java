package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    EditText username, password, confirmPassword;
    Button registerBtn;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        username = findViewById(R.id.signup_username);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerBtn = findViewById(R.id.registerBtn);
        loginText = findViewById(R.id.loginText);

        registerBtn.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String confirm = confirmPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                AppDatabase db = AppDatabase.getInstance(this);

                if (db.userDao().checkUser(user) != null) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                } else {
                    db.userDao().registerUser(new User(user, pass));
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}