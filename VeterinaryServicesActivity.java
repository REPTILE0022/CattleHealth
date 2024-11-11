package com.example.cattlehealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;

public class VeterinaryServicesActivity extends AppCompatActivity {
    private Button btnDeworming, btnDehorning, btnInsemination, btnVaccination, btnDiseaseTreatment;
    private CheckBox cbShareLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veterinary_services);

        btnDeworming = findViewById(R.id.btnDeworming);
        btnDehorning = findViewById(R.id.btnDehorning);
        btnInsemination = findViewById(R.id.btnInsemination);
        btnVaccination = findViewById(R.id.btnVaccination);
        btnDiseaseTreatment = findViewById(R.id.btnDiseaseTreatment);
        cbShareLocation = findViewById(R.id.cbShareLocation);

        View.OnClickListener serviceClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VeterinaryServicesActivity.this, ScheduleAppointmentActivity.class);
                intent.putExtra("shareLocation", cbShareLocation.isChecked());
                startActivity(intent);
            }
        };

        btnDeworming.setOnClickListener(serviceClickListener);
        btnDehorning.setOnClickListener(serviceClickListener);
        btnInsemination.setOnClickListener(serviceClickListener);
        btnVaccination.setOnClickListener(serviceClickListener);
        btnDiseaseTreatment.setOnClickListener(serviceClickListener);
    }
}
