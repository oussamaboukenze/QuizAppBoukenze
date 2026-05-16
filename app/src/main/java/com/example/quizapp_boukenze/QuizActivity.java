package com.example.quizapp_boukenze;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class QuizActivity extends AppCompatActivity {
    private TextView tvProgress, tvQuestion;
    private RadioGroup rgOptions;
    private ScrollView quizScroll;
    private Button bNext;
    private FloatingActionButton fabChatbot;

    private List<QuestionWithheld> quizData;
    private int currentQuestionIndex = 0;
    private final Map<String, Integer> scores = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_dynamic);
        SystemBarHelper.apply(findViewById(R.id.rootView), 0, 0, 0);

        tvProgress = findViewById(R.id.tvProgress);
        tvQuestion = findViewById(R.id.tvQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        quizScroll = findViewById(R.id.quizScroll);
        bNext = findViewById(R.id.bNext);
        fabChatbot = findViewById(R.id.fabChatbot);
        bNext.setEnabled(false);

        initScores();
        loadQuiz();

        bNext.setOnClickListener(v -> handleNextQuestion());
        fabChatbot.setOnClickListener(v -> openChatbot());
    }

    private void initScores() {
        scores.put("IIR", 0);
        scores.put("GESI", 0);
        scores.put("IAII", 0);
        scores.put("GC", 0);
        scores.put("GI", 0);
        scores.put("GF", 0);
    }

    private void handleNextQuestion() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Veuillez choisir une reponse", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedRb = findViewById(selectedId);
        String majorCode = (String) selectedRb.getTag();
        if (scores.containsKey(majorCode)) {
            scores.put(majorCode, scores.get(majorCode) + 1);
        }

        currentQuestionIndex++;
        if (quizData != null && currentQuestionIndex < quizData.size()) {
            displayQuestion();
        } else {
            finishQuiz();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadQuiz() {
        new Thread(() -> {
            try {
                quizData = (List<QuestionWithheld>) BuildersKt.runBlocking(
                        EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> AuthHelper.INSTANCE.getQuizQuestions(continuation)
                );

                runOnUiThread(() -> {
                    if (quizData != null && !quizData.isEmpty()) {
                        displayQuestion();
                    } else {
                        Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_LONG).show();
                    finish();
                });
            }
        }).start();
    }

    private void displayQuestion() {
        if (quizData == null || currentQuestionIndex >= quizData.size()) {
            return;
        }

        QuestionWithheld current = quizData.get(currentQuestionIndex);
        bNext.setEnabled(true);
        tvProgress.setText("Question " + (currentQuestionIndex + 1) + "/" + quizData.size());
        tvQuestion.setText(current.getQuestion().getQuestion_text());
        bNext.setText(currentQuestionIndex == quizData.size() - 1 ? "Voir mon resultat" : "Continuer");

        rgOptions.removeAllViews();
        for (QuestionOption option : current.getOptions()) {
            RadioButton rb = new RadioButton(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, dp(10));

            rb.setText(option.getOption_text());
            rb.setTag(option.getTarget_major_code());
            rb.setLayoutParams(params);
            rb.setMinHeight(dp(58));
            rb.setPadding(dp(14), dp(10), dp(14), dp(10));
            rb.setTextColor(Color.BLACK);
            rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            rb.setBackgroundResource(R.drawable.bg_metric_row);
            rb.setButtonTintList(ColorStateList.valueOf(getColor(R.color.emsi_green)));
            rgOptions.addView(rb);
        }
        rgOptions.clearCheck();
        quizScroll.post(() -> quizScroll.smoothScrollTo(0, 0));
    }

    private void openChatbot() {
        if (quizData == null || quizData.isEmpty()) {
            Toast.makeText(this, "Veuillez attendre le chargement", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(QuizActivity.this, ChatbotActivity.class);
        if (currentQuestionIndex < quizData.size()) {
            QuestionWithheld current = quizData.get(currentQuestionIndex);
            String context = "L'utilisateur passe le quiz d'orientation EMSI. Question actuelle: \""
                    + current.getQuestion().getQuestion_text()
                    + "\". Reponds en tenant compte de cette question si c'est utile.";
            intent.putExtra(ChatbotActivity.EXTRA_CONTEXT, context);
        }
        startActivity(intent);
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

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }
}
