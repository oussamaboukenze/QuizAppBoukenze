package com.example.quizapp_boukenze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class MainActivity extends AppCompatActivity {
    EditText etMail, etPassword;
    Button bLogin, bChatbot;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemBarHelper.apply(findViewById(R.id.rootView), 0, 0, 0);

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.bLogin);
        bChatbot = findViewById(R.id.bChatbot);
        tvRegister = findViewById(R.id.tvRegister);

        bLogin.setOnClickListener(v -> {
            String mail = etMail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                setLoginLoading(true);
                loginUser(mail, password);
            }
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });

        bChatbot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password) {
        new Thread(() -> {
            try {
                // Call suspend function from Java using runBlocking
                BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, (scope, continuation) ->
                        AuthHelper.INSTANCE.login(email, password, continuation));

                UserScore latestScore = null;
                try {
                    // Check if user has previous scores, but do not block login if scores are unavailable.
                    latestScore = BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, (scope, continuation) ->
                            AuthHelper.INSTANCE.getLatestScore(continuation));
                } catch (Exception ignored) {
                    latestScore = null;
                }
                
                UserScore scoreToOpen = latestScore;
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Connexion reussie", Toast.LENGTH_SHORT).show();
                    
                    if (scoreToOpen != null) {
                        // Redirect to Score screen if they have results
                        Intent intent = new Intent(MainActivity.this, Score.class);
                        intent.putExtra("scoreIIR", scoreToOpen.getScore_iir());
                        intent.putExtra("scoreGESI", scoreToOpen.getScore_gesi());
                        intent.putExtra("scoreIAII", scoreToOpen.getScore_iaii());
                        intent.putExtra("scoreGC", scoreToOpen.getScore_gc());
                        intent.putExtra("scoreGI", scoreToOpen.getScore_gi());
                        intent.putExtra("scoreGF", scoreToOpen.getScore_gf());
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
                    setLoginLoading(false);
                    Toast.makeText(MainActivity.this, "Connexion impossible: " + (e.getMessage() != null ? e.getMessage() : "Erreur"), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void setLoginLoading(boolean loading) {
        bLogin.setEnabled(!loading);
        bLogin.setText(loading ? "Connexion..." : "Se connecter");
    }
}
