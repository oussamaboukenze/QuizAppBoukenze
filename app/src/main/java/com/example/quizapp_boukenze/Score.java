package com.example.quizapp_boukenze;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class Score extends AppCompatActivity {
    private RadarChart radarChart;
    private ProgressBar pbIIR, pbGESI, pbIAII, pbGC, pbGI, pbGF;
    private TextView tvStatus, tvRecommendation;
    private Button bTry, bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        SystemBarHelper.apply(findViewById(R.id.rootView), 0, 0, 0);

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

        Intent intent = getIntent();
        int sIIR = intent.getIntExtra("scoreIIR", 0);
        int sGESI = intent.getIntExtra("scoreGESI", 0);
        int sIAII = intent.getIntExtra("scoreIAII", 0);
        int sGC = intent.getIntExtra("scoreGC", 0);
        int sGI = intent.getIntExtra("scoreGI", 0);
        int sGF = intent.getIntExtra("scoreGF", 0);

        int maxScore = Math.max(1, Math.max(sIIR, Math.max(sGESI, Math.max(sIAII, Math.max(sGC, Math.max(sGI, sGF))))));
        String recommendedMajor = getRecommendedMajor(sIIR, sGESI, sIAII, sGC, sGI, sGF);

        tvStatus.setText("Filiere recommandee");
        tvRecommendation.setText(recommendedMajor);

        updateProgressBars(maxScore, sIIR, sGESI, sIAII, sGC, sGI, sGF);
        setupRadarChart(maxScore, sIIR, sGESI, sIAII, sGC, sGI, sGF);

        if (intent.hasExtra("isNewScore")) {
            saveScoresToDb(sIIR, sGESI, sIAII, sGC, sGI, sGF);
        }

        bTry.setOnClickListener(v -> {
            startActivity(new Intent(Score.this, QuizActivity.class));
            finish();
        });

        bLogout.setOnClickListener(v -> finish());
    }

    private String getRecommendedMajor(int iir, int gesi, int iaii, int gc, int gi, int gf) {
        String major = "IIR - Ingenierie Informatique et Reseaux";
        int max = iir;

        if (gesi > max) { max = gesi; major = "GESI - Genie Electrique et Systemes Intelligents"; }
        if (iaii > max) { max = iaii; major = "IAII - Intelligence Artificielle et Ingenierie Informatique"; }
        if (gc > max) { max = gc; major = "GC - Genie Civil"; }
        if (gi > max) { max = gi; major = "GI - Genie Industriel"; }
        if (gf > max) { major = "GF - Genie Financier"; }

        return major;
    }

    private void updateProgressBars(int maxScore, int iir, int gesi, int iaii, int gc, int gi, int gf) {
        pbIIR.setProgress(toPercent(iir, maxScore));
        pbGESI.setProgress(toPercent(gesi, maxScore));
        pbIAII.setProgress(toPercent(iaii, maxScore));
        pbGC.setProgress(toPercent(gc, maxScore));
        pbGI.setProgress(toPercent(gi, maxScore));
        pbGF.setProgress(toPercent(gf, maxScore));
    }

    private int toPercent(int score, int maxScore) {
        return Math.round((score * 100f) / maxScore);
    }

    private void saveScoresToDb(int iir, int gesi, int iaii, int gc, int gi, int gf) {
        new Thread(() -> {
            try {
                BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE, (scope, continuation) ->
                        AuthHelper.INSTANCE.saveScore(iir, gesi, iaii, gc, gi, gf, continuation));

                runOnUiThread(() -> Toast.makeText(Score.this, "Scores sauvegardes", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(Score.this, "Sauvegarde impossible: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void setupRadarChart(int maxScore, int iir, int gesi, int iaii, int gc, int gi, int gf) {
        ArrayList<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(iir));
        entries.add(new RadarEntry(gesi));
        entries.add(new RadarEntry(iaii));
        entries.add(new RadarEntry(gc));
        entries.add(new RadarEntry(gi));
        entries.add(new RadarEntry(gf));

        RadarDataSet dataSet = new RadarDataSet(entries, "Interets");
        dataSet.setColor(Color.parseColor("#007A3D"));
        dataSet.setFillColor(Color.parseColor("#007A3D"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(85);
        dataSet.setLineWidth(2f);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setDrawValues(false);

        radarChart.setData(new RadarData(dataSet));
        radarChart.getDescription().setEnabled(false);
        radarChart.getLegend().setEnabled(false);
        radarChart.setRotationEnabled(false);
        radarChart.setWebColor(Color.parseColor("#DCE8E1"));
        radarChart.setWebColorInner(Color.parseColor("#DCE8E1"));
        radarChart.setWebLineWidth(1f);
        radarChart.setWebLineWidthInner(1f);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#101915"));
        xAxis.setTextSize(11f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"IIR", "GESI", "IAII", "GC", "GI", "GF"}));

        radarChart.getYAxis().setEnabled(false);
        radarChart.getYAxis().setAxisMinimum(0f);
        radarChart.getYAxis().setAxisMaximum(Math.max(5f, maxScore));
        radarChart.invalidate();
    }

}
