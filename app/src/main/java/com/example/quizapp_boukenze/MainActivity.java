package com.example.quizapp_boukenze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText etMail, etPassword;
    Button bLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.bLogin);
        tvRegister = findViewById(R.id.tvRegister);

        bLogin.setOnClickListener(v -> {
            String mail = etMail.getText().toString();
            String password = etPassword.getText().toString();

            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(mail, password);
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        new Thread(() -> {
            try {
                SupabaseAuthHelper.login(email, password);
                
                // Check if user has previous scores
                UserScore latestScore = SupabaseAuthHelper.getLatestScore();
                
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    
                    if (latestScore != null) {
                        // Redirect to Score screen if they have results
                        Intent intent = new Intent(MainActivity.this, Score.class);
                        intent.putExtra("scoreIIR", latestScore.getScore_iir());
                        intent.putExtra("scoreGESI", latestScore.getScore_gesi());
                        intent.putExtra("scoreIAII", latestScore.getScore_iaii());
                        intent.putExtra("scoreGC", latestScore.getScore_gc());
                        intent.putExtra("scoreGI", latestScore.getScore_gi());
                        intent.putExtra("scoreGF", latestScore.getScore_gf());
                        // No "isNewScore" here because we are just viewing old results
                        startActivity(intent);
                    } else {
                        // Redirect to the new Dynamic Quiz
                        Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                        startActivity(intent);
                    }
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Login Failed: " + (e.getMessage() != null ? e.getMessage() : "Error"), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
