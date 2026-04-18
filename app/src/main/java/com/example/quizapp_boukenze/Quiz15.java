package com.example.quizapp_boukenze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Quiz15 extends AppCompatActivity {
    RadioGroup rg;
    Button bNext;
    int sIIR, sGESI, sIAII, sGC, sGI, sGF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz15);
        
        rg = findViewById(R.id.rg);
        bNext = findViewById(R.id.bNext);
        
        Intent intent = getIntent();
        sIIR = intent.getIntExtra("sIIR", 0);
        sGESI = intent.getIntExtra("sGESI", 0);
        sIAII = intent.getIntExtra("sIAII", 0);
        sGC = intent.getIntExtra("sGC", 0);
        sGI = intent.getIntExtra("sGI", 0);
        sGF = intent.getIntExtra("sGF", 0);

        bNext.setOnClickListener(v -> {
            int selectedId = rg.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getApplicationContext(), "Diagnostic presque terminé, merci de répondre.", Toast.LENGTH_SHORT).show();
            } else {
                if (selectedId == R.id.rb1) sIIR++;
                else if (selectedId == R.id.rb2) sGC++;
                else if (selectedId == R.id.rb3) sGF++;
                else if (selectedId == R.id.rb4) sGI++;

                Intent intentNext = new Intent(Quiz15.this, Score.class);
                intentNext.putExtra("scoreIIR", sIIR);
                intentNext.putExtra("scoreGESI", sGESI);
                intentNext.putExtra("scoreIAII", sIAII);
                intentNext.putExtra("scoreGC", sGC);
                intentNext.putExtra("scoreGI", sGI);
                intentNext.putExtra("scoreGF", sGF);
                startActivity(intentNext);
                finish();
            }
        });
    }
}
