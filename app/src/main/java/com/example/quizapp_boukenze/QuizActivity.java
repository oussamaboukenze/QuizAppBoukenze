package com.example.quizapp_boukenze;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
    TextView tvProgress, tvQuestion;
    RadioGroup rgOptions;
    Button bNext;

    List<QuestionWithheld> quizData;
    int currentQuestionIndex = 0;
    
    // Scores
    Map<String, Integer> scores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dynamic);

        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        bNext = findViewById(R.id.bNext);

        // Initialize scores
        scores.put("IIR", 0);
        scores.put("GESI", 0);
        scores.put("IAII", 0);
        scores.put("GC", 0);
        scores.put("GI", 0);
        scores.put("GF", 0);

        loadQuiz();

        bNext.setOnClickListener(v -> {
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Veuillez choisir une réponse", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update score
            RadioButton selectedRb = findViewById(selectedId);
            String majorCode = (String) selectedRb.getTag();
            if (scores.containsKey(majorCode)) {
                scores.put(majorCode, scores.get(majorCode) + 1);
            }

            // Move to next question or finish
            currentQuestionIndex++;
            if (currentQuestionIndex < quizData.size()) {
                displayQuestion();
            } else {
                finishQuiz();
            }
        });
    }

    private void loadQuiz() {
        new Thread(() -> {
            quizData = SupabaseAuthHelper.getQuizQuestions();
            runOnUiThread(() -> {
                if (quizData != null && !quizData.isEmpty()) {
                    displayQuestion();
                } else {
                    Toast.makeText(this, "Impossible de charger les questions.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }).start();
    }

    private void displayQuestion() {
        QuestionWithheld current = quizData.get(currentQuestionIndex);
        
        tvProgress.setText("Question " + (currentQuestionIndex + 1) + "/" + quizData.size());
        tvQuestion.setText(current.getQuestion().getQuestion_text());
        
        rgOptions.removeAllViews();
        for (QuestionOption option : current.getOptions()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option.getOption_text());
            rb.setTag(option.getTarget_major_code());
            rb.setPadding(10, 20, 10, 20);
            rb.setTextColor(Color.BLACK);
            rgOptions.addView(rb);
        }
        rgOptions.clearCheck();
    }

    private void finishQuiz() {
        Intent intent = new Intent(QuizActivity.this, Score.class);
        intent.putExtra("scoreIIR", scores.get("IIR"));
        intent.putExtra("scoreGESI", scores.get("GESI"));
        intent.putExtra("scoreIAII", scores.get("IAII"));
        intent.putExtra("scoreGC", scores.get("GC"));
        intent.putExtra("scoreGI", scores.get("GI"));
        intent.putExtra("scoreGF", scores.get("GF"));
        intent.putExtra("isNewScore", true);
        startActivity(intent);
        finish();
    }
}
