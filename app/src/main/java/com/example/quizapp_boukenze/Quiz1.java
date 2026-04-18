package com.example.quizapp_boukenze;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Quiz1 extends AppCompatActivity {
    RadioGroup rg;
    Button bNext;
    
    // Scores par filière
    int sIIR = 0, sGESI = 0, sIAII = 0, sGC = 0, sGI = 0, sGF = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz1);
        
        rg = findViewById(R.id.rg);
        bNext = findViewById(R.id.bNext);
        
        bNext.setOnClickListener(v -> {
            int selectedId = rg.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(getApplicationContext(), "Merci de choisir une réponse !", Toast.LENGTH_SHORT).show();
            } else {
                if (selectedId == R.id.rb1) sIIR++;
                else if (selectedId == R.id.rb2) sGESI++;
                else if (selectedId == R.id.rb3) sIAII++;
                else if (selectedId == R.id.rb4) sGF++;

                Intent intent = new Intent(Quiz1.this, Quiz2.class);
                intent.putExtra("sIIR", sIIR);
                intent.putExtra("sGESI", sGESI);
                intent.putExtra("sIAII", sIAII);
                intent.putExtra("sGC", sGC);
                intent.putExtra("sGI", sGI);
                intent.putExtra("sGF", sGF);
                startActivity(intent);
                finish();
            }
        });
    }
}
