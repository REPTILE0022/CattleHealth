package com.example.cattlehealth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ScheduleAppointmentActivity extends AppCompatActivity {
    private EditText etDate, etLocation, etAnimalKind, etAge, etContactInfo, etName;
    private Button btnSubmit;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_appointment);

        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etAnimalKind = findViewById(R.id.etAnimalKind);
        etAge = findViewById(R.id.etAge);
        etContactInfo = findViewById(R.id.etContactInfo);
        etName = findViewById(R.id.etName);
        btnSubmit = findViewById(R.id.btnSubmit);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnSubmit.setOnClickListener(view -> {
            // Get the appointment details entered by the user
            String date = etDate.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String animalKind = etAnimalKind.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String contactInfo = etContactInfo.getText().toString().trim();
            String name = etName.getText().toString().trim();

            // Validate fields
            if (TextUtils.isEmpty(date) || !isValidDate(date)) {
                etDate.setError("Please enter a valid date (format: dd/MM/yyyy)");
                return;
            }

            if (TextUtils.isEmpty(contactInfo)) {
                etContactInfo.setError("Please enter contact information");
                return;
            }

            if (TextUtils.isEmpty(name)) {
                etName.setError("Please enter your name");
                return;
            }

            // Get the current user ID
            String userId = mAuth.getCurrentUser().getUid();

            // Create a unique key for the appointment
            String appointmentKey = mDatabase.child("appointments").push().getKey();

            // Create a HashMap to represent appointment data
            HashMap<String, Object> appointmentData = new HashMap<>();
            appointmentData.put("date", date);
            appointmentData.put("location", location);
            appointmentData.put("animalKind", animalKind);
            appointmentData.put("age", age);
            appointmentData.put("contactInfo", contactInfo);
            appointmentData.put("name", name);

            // Save appointment data to the user's node in the database
            mDatabase.child("users").child(userId).child("appointments").child(appointmentKey).setValue(appointmentData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Appointment saved successfully
                            Toast.makeText(ScheduleAppointmentActivity.this, "Appointment scheduled successfully", Toast.LENGTH_SHORT).show();
                            // You can add further actions here, such as navigating back to the previous activity
                        } else {
                            // Appointment saving failed
                            Toast.makeText(ScheduleAppointmentActivity.this, "Failed to schedule appointment", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Request location permissions if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Handle location object
                        String locationString = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
                        etLocation.setText(locationString);
                    } else {
                        // Request new location if last known location is null
                        requestNewLocation();
                    }
                }
            });
        }
    }

    private void requestNewLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Handle new location
                    String locationString = "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude();
                    etLocation.setText(locationString);
                    fusedLocationClient.removeLocationUpdates(locationCallback); // Stop location updates
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to check if the date string is in valid format
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false); // To strict parsing, any date not in this format will throw ParseException
        try {
            Date date = sdf.parse(dateStr);
            // Date format is valid
            return true;
        } catch (ParseException e) {
            // Date format is invalid
            return false;
        }
    }
}
