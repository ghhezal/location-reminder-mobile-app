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
    EditText username;
    EditText password;
    Button loginbtn;
    TextView signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_activity);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginbtn = findViewById(R.id.loginbtn);
        signUp = findViewById(R.id.signUp);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (user.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter username", Toast.LENGTH_SHORT).show();
                }
                else if (pass.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter password", Toast.LENGTH_SHORT).show();
                }
                else {

                    AppDatabase db = AppDatabase.getInstance(Login.this);
                    User registeredUser = db.userDao().login(user, pass);

                    if (registeredUser != null) {

                        Intent intent = new Intent(Login.this, HomeActivity.class);
                        intent.putExtra("USER_NAME", user);
                        startActivity(intent);
                        finish();
                    } else {

                        Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
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