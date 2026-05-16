package com.example.quizapp_boukenze;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class Score extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST = 2001;
    private static final List<CampusSpot> CAMPUS_SPOTS = Arrays.asList(
            new CampusSpot("EMSI Centre - Casablanca", 33.5731, -7.5898),
            new CampusSpot("EMSI Maarif - Casablanca", 33.5866, -7.6336),
            new CampusSpot("EMSI Moulay Youssef - Casablanca", 33.5967, -7.6263),
            new CampusSpot("EMSI Les Orangers - Rabat", 34.0060, -6.8498),
            new CampusSpot("EMSI Agdal - Rabat", 34.0007, -6.8483),
            new CampusSpot("EMSI Gueliz - Marrakech", 31.6342, -8.0108),
            new CampusSpot("EMSI Tanger", 35.7595, -5.8340)
    );

    private RadarChart radarChart;
    private ProgressBar pbIIR, pbGESI, pbIAII, pbGC, pbGI, pbGF;
    private TextView tvStatus, tvRecommendation, tvNearestCampus;
    private Button bTry, bLogout, bFindCampus, bOpenCampusMap;
    private CampusSpot selectedCampus;

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
        tvNearestCampus = findViewById(R.id.tvNearestCampus);
        bTry = findViewById(R.id.bTry);
        bLogout = findViewById(R.id.bLogout);
        bFindCampus = findViewById(R.id.bFindCampus);
        bOpenCampusMap = findViewById(R.id.bOpenCampusMap);
        bOpenCampusMap.setEnabled(false);
        bOpenCampusMap.setAlpha(0.45f);

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
        bFindCampus.setOnClickListener(v -> findNearestCampus());
        bOpenCampusMap.setOnClickListener(v -> openSelectedCampusInMaps());
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

    private void findNearestCampus() {
        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        resolveNearestCampus();
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void resolveNearestCampus() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            tvNearestCampus.setText("Service GPS indisponible sur ce telephone.");
            return;
        }

        try {
            Location lastLocation = getBestLastKnownLocation(locationManager);
            if (lastLocation != null) {
                updateNearestCampus(lastLocation);
                return;
            }

            requestFreshLocation(locationManager);
        } catch (SecurityException e) {
            tvNearestCampus.setText("Permission GPS requise pour trouver le campus le plus proche.");
        }
    }

    private Location getBestLastKnownLocation(LocationManager locationManager) {
        Location bestLocation = null;
        for (String provider : locationManager.getProviders(true)) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null && (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy())) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    private void requestFreshLocation(LocationManager locationManager) {
        List<String> providers = locationManager.getProviders(true);
        String provider = providers.contains(LocationManager.NETWORK_PROVIDER)
                ? LocationManager.NETWORK_PROVIDER
                : providers.contains(LocationManager.GPS_PROVIDER) ? LocationManager.GPS_PROVIDER : null;

        if (provider == null) {
            tvNearestCampus.setText("Activez la localisation du telephone pour trouver le campus le plus proche.");
            return;
        }

        tvNearestCampus.setText("Localisation en cours...");
        locationManager.requestSingleUpdate(provider, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateNearestCampus(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // Android legacy callback.
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // No UI update needed.
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                tvNearestCampus.setText("Activez la localisation du telephone pour continuer.");
            }
        }, Looper.getMainLooper());
    }

    private void updateNearestCampus(Location userLocation) {
        CampusSpot nearest = findNearestCampusFor(userLocation);
        selectedCampus = nearest;
        float distanceMeters = distanceTo(nearest, userLocation);
        String distanceText = distanceMeters >= 1000f
                ? String.format(Locale.FRANCE, "%.1f km", distanceMeters / 1000f)
                : String.format(Locale.FRANCE, "%.0f m", distanceMeters);

        tvNearestCampus.setText(nearest.name + "\nDistance approximative: " + distanceText);
        bOpenCampusMap.setEnabled(true);
        bOpenCampusMap.setAlpha(1f);
    }

    private CampusSpot findNearestCampusFor(Location userLocation) {
        CampusSpot nearest = CAMPUS_SPOTS.get(0);
        float nearestDistance = distanceTo(nearest, userLocation);

        for (int i = 1; i < CAMPUS_SPOTS.size(); i++) {
            CampusSpot campus = CAMPUS_SPOTS.get(i);
            float distance = distanceTo(campus, userLocation);
            if (distance < nearestDistance) {
                nearest = campus;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private float distanceTo(CampusSpot campus, Location userLocation) {
        Location campusLocation = new Location(campus.name);
        campusLocation.setLatitude(campus.latitude);
        campusLocation.setLongitude(campus.longitude);
        return userLocation.distanceTo(campusLocation);
    }

    private void openSelectedCampusInMaps() {
        if (selectedCampus == null) {
            findNearestCampus();
            return;
        }

        Uri uri = Uri.parse("geo:" + selectedCampus.latitude + "," + selectedCampus.longitude
                + "?q=" + selectedCampus.latitude + "," + selectedCampus.longitude
                + "(" + Uri.encode(selectedCampus.name) + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);

        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Aucune application Maps trouvee", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST) {
            return;
        }

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                resolveNearestCampus();
                return;
            }
        }
        tvNearestCampus.setText("Permission GPS refusee. Le campus le plus proche ne peut pas etre calcule.");
    }

    private static final class CampusSpot {
        private final String name;
        private final double latitude;
        private final double longitude;

        private CampusSpot(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
