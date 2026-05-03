package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;

public class Login extends AppCompatActivity {
    EditText email; // Changed from username
    EditText password;
    Button loginbtn;
    TextView signUp;
    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        email = findViewById(R.id.email); // Ensure ID in XML is @+id/email
        password = findViewById(R.id.password);
        loginbtn = findViewById(R.id.loginbtn);
        signUp = findViewById(R.id.signUp);
        forgotPassword = findViewById(R.id.forgotPassword);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        // Forgot Password Logic using Email
        forgotPassword.setOnClickListener(v -> {
            String emailStr = email.getText().toString().trim();

            if (emailStr.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
            } else {
                AppDatabase db = AppDatabase.getInstance(this);
                String hint = db.userDao().getPasswordHint(emailStr);

                if (hint != null && !hint.isEmpty()) {
                    Toast.makeText(this, "Hint: " + hint, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No hint found for this email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (emailStr.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
                else if (pass.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter password", Toast.LENGTH_SHORT).show();
                }
                else {
                    AppDatabase db = AppDatabase.getInstance(Login.this);
                    // Fetch user by email
                    User registeredUser = db.userDao().checkUser(emailStr);

                    // Verify hashed password
                    if (registeredUser != null && PasswordUtils.verifyPassword(pass, registeredUser.password)) {
                        Intent intent = new Intent(Login.this, HomeActivity.class);
                        // Pass USER_EMAIL to the Home screen
                        intent.putExtra("USER_EMAIL", emailStr);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}