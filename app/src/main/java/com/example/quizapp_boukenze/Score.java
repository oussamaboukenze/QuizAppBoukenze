package com.example.quizapp_boukenze;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class Score extends AppCompatActivity {
    RadarChart radarChart;
    ProgressBar pbIIR, pbGESI, pbIAII, pbGC, pbGI, pbGF;
    TextView tvStatus, tvRecommendation;
    Button bTry, bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Initialize views
        radarChart = findViewById(R.id.radarChart);
        pbIIR = findViewById(R.id.pbIIR);
        pbGESI = findViewById(R.id.pbGESI);
        pbIAII = findViewById(R.id.pbIAII);
        pbGC = findViewById(R.id.pbGC);
        pbGI = findViewById(R.id.pbGI);
        pbGF = findViewById(R.id.pbGF);
        
        tvStatus = findViewById(R.id.tvStatus);
        tvRecommendation = findViewById(R.id.tvRecommendation);
        bTry = findViewById(R.id.bTry);
        bLogout = findViewById(R.id.bLogout);

        // Get scores from intent
        Intent intent = getIntent();
        int sIIR = intent.getIntExtra("scoreIIR", 0);
        int sGESI = intent.getIntExtra("scoreGESI", 0);
        int sIAII = intent.getIntExtra("scoreIAII", 0);
        int sGC = intent.getIntExtra("scoreGC", 0);
        int sGI = intent.getIntExtra("scoreGI", 0);
        int sGF = intent.getIntExtra("scoreGF", 0);

        // Identify top major
        String recommendedMajor = "IIR";
        int maxScore = sIIR;
        
        if (sGESI > maxScore) { maxScore = sGESI; recommendedMajor = "GESI"; }
        if (sIAII > maxScore) { maxScore = sIAII; recommendedMajor = "IAII"; }
        if (sGC > maxScore) { maxScore = sGC; recommendedMajor = "GC (Génie Civil)"; }
        if (sGI > maxScore) { maxScore = sGI; recommendedMajor = "GI (Génie Industriel)"; }
        if (sGF > maxScore) { maxScore = sGF; recommendedMajor = "GF (Génie Financier)"; }

        tvRecommendation.setText("Basé sur vos intérêts, la filière recommandée est : " + recommendedMajor);

        // Update ProgressBars (assuming max 5 questions per category for weighting)
        pbIIR.setProgress(sIIR * 20); 
        pbGESI.setProgress(sGESI * 20);
        pbIAII.setProgress(sIAII * 20);
        pbGC.setProgress(sGC * 20);
        pbGI.setProgress(sGI * 20);
        pbGF.setProgress(sGF * 20);

        // Setup Radar Chart
        setupRadarChart(sIIR, sGESI, sIAII, sGC, sGI, sGF);

        bTry.setOnClickListener(v -> {
            startActivity(new Intent(Score.this, Quiz1.class));
            finish();
        });

        bLogout.setOnClickListener(v -> finish());
    }

    private void setupRadarChart(int iir, int gesi, int iaii, int gc, int gi, int gf) {
        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(iir));
        entries.add(new RadarEntry(gesi));
        entries.add(new RadarEntry(iaii));
        entries.add(new RadarEntry(gc));
        entries.add(new RadarEntry(gi));
        entries.add(new RadarEntry(gf));

        RadarDataSet dataSet = new RadarDataSet(entries, "Intérêts");
        dataSet.setColor(Color.parseColor("#008542")); // EMSI Green
        dataSet.setFillColor(Color.parseColor("#008542"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(100);
        dataSet.setLineWidth(2f);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setDrawValues(false);

        RadarData data = new RadarData(dataSet);
        radarChart.setData(data);
        radarChart.getDescription().setEnabled(false);
        radarChart.getLegend().setEnabled(false);
        
        radarChart.setWebColor(Color.LTGRAY);
        radarChart.setWebColorInner(Color.LTGRAY);
        radarChart.setWebLineWidth(1f);
        radarChart.setWebLineWidthInner(1f);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(10f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"IIR", "GESI", "IAII", "GC", "GI", "GF"}));

        radarChart.getYAxis().setEnabled(false);
        radarChart.getYAxis().setAxisMinimum(0f);
        radarChart.getYAxis().setAxisMaximum(5f);

        radarChart.invalidate();
    }
}
