package com.example.quizapp_boukenze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Quiz5 extends AppCompatActivity {
    RadioGroup rg;
    Button bNext;
    int sIIR, sGESI, sIAII, sGC, sGI, sGF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz5);
        
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
                Toast.makeText(getApplicationContext(), "Merci de répondre à la question !", Toast.LENGTH_SHORT).show();
            } else {
                if (selectedId == R.id.rb1) sGF++;
                else if (selectedId == R.id.rb2) sGESI++;
                else if (selectedId == R.id.rb3) sIIR++;
                else if (selectedId == R.id.rb4) sGF++;

                Intent intentNext = new Intent(Quiz5.this, Quiz6.class);
                intentNext.putExtra("sIIR", sIIR);
                intentNext.putExtra("sGESI", sGESI);
                intentNext.putExtra("sIAII", sIAII);
                intentNext.putExtra("sGC", sGC);
                intentNext.putExtra("sGI", sGI);
                intentNext.putExtra("sGF", sGF);
                startActivity(intentNext);
                finish();
            }
        });
    }
}
