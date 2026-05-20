package com.example.quizapp_boukenze;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EmsiMapActivity extends AppCompatActivity {
    private static final String MAPS_PACKAGE = "com.google.android.apps.maps";
    private static final String SEARCH_QUERY = "EMSI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openGoogleMapsSearch();
        finish();
    }

    private void openGoogleMapsSearch() {
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(SEARCH_QUERY));
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        mapsIntent.setPackage(MAPS_PACKAGE);

        try {
            startActivity(mapsIntent);
        } catch (ActivityNotFoundException error) {
            openMapsInBrowser();
        }
    }

    private void openMapsInBrowser() {
        Uri browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(SEARCH_QUERY));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);

        try {
            startActivity(browserIntent);
        } catch (ActivityNotFoundException error) {
            Toast.makeText(this, "Google Maps indisponible sur cet appareil", Toast.LENGTH_LONG).show();
        }
    }
}
