package com.example.quizapp_boukenze;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Register extends AppCompatActivity {
    EditText etMail, etPassword, etPassword1, etName, etSchool;
    Button bRegister;
    ImageView ivProfile;
    FloatingActionButton fabAddPhoto;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private static class CampusArea {
        String name;
        double minLat, maxLat;
        double minLon, maxLon;

        CampusArea(String name, double minLat, double maxLat, double minLon, double maxLon) {
            this.name = name;
            this.minLat = minLat;
            this.maxLat = maxLat;
            this.minLon = minLon;
            this.maxLon = maxLon;
        }

        boolean isInside(double lat, double lon) {
            return lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon;
        }
    }

    private List<CampusArea> emsiAreas;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    ivProfile.setImageURI(imageUri);
                    // Note: Here you would ideally add face detection logic
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    ivProfile.setImageBitmap(photo);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initCampusAreas();

        etName = findViewById(R.id.etName);
        etMail = findViewById(R.id.etMail);
        etSchool = findViewById(R.id.etSchool);
        etPassword = findViewById(R.id.etPassword);
        etPassword1 = findViewById(R.id.etPassword1);
        bRegister = findViewById(R.id.bRegister);
        ivProfile = findViewById(R.id.ivProfile);
        fabAddPhoto = findViewById(R.id.fabAddPhoto);

        etSchool.setFocusable(false);
        etSchool.setClickable(false);
        etSchool.setFocusableInTouchMode(false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        detectLocation();

        fabAddPhoto.setOnClickListener(v -> showImageSourceDialog());

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void showImageSourceDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Select Profile Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(takePicture);
                    } else {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(pickPhoto);
                    }
                })
                .show();
    }

    private void initCampusAreas() {
        emsiAreas = new ArrayList<>();
        emsiAreas.add(new CampusArea("EMSI Orangers", 33.5000, 33.6500, -7.7500, -7.5000));
        emsiAreas.add(new CampusArea("EMSI Centre", 33.5800, 33.5950, -7.6400, -7.6200));
        emsiAreas.add(new CampusArea("EMSI Maarif", 33.5700, 33.5850, -7.6500, -7.6300));
        emsiAreas.add(new CampusArea("EMSI Moulay Youssef", 33.5900, 33.6100, -7.6350, -7.6150));
        emsiAreas.add(new CampusArea("EMSI Orangers", 33.9900, 34.0200, -6.8600, -6.8300));
        emsiAreas.add(new CampusArea("EMSI Agdal", 33.9950, 34.0100, -6.8650, -6.8400));
        emsiAreas.add(new CampusArea("EMSI Guéliz", 31.6200, 31.6500, -8.0300, -7.9900));
    }

    private void detectLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            checkCampusInterval(location.getLatitude(), location.getLongitude());
                        } else {
                            fusedLocationClient.getLastLocation().addOnSuccessListener(Register.this, lastLoc -> {
                                if (lastLoc != null) {
                                    checkCampusInterval(lastLoc.getLatitude(), lastLoc.getLongitude());
                                } else {
                                    etSchool.setHint("Localisation non trouvée");
                                }
                            });
                        }
                    }
                });
    }

    private void checkCampusInterval(double latitude, double longitude) {
        for (CampusArea area : emsiAreas) {
            if (area.isInside(latitude, longitude)) {
                etSchool.setText(area.name);
                return;
            }
        }
        etSchool.setText("Etablissement Hors Zone EMSI");
    }

    private void registerUser() {
        String mail = etMail.getText().toString();
        String password = etPassword.getText().toString();
        String password1 = etPassword1.getText().toString();

        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password1)) {
            Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(password1)) {
            Toast.makeText(getApplicationContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(), "Inscription réussie !", Toast.LENGTH_LONG).show();
        startActivity(new Intent(Register.this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectLocation();
            } else {
                Toast.makeText(this, "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
