package com.example.cattlehealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class UserDashboardActivity extends AppCompatActivity {
    private Button btnAccessVetServices, btnHealthRecords, btnChatForum, btnAccessAIServices,btnMakePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        btnAccessVetServices = findViewById(R.id.btnAccessVetServices);
        btnHealthRecords = findViewById(R.id.btnHealthRecords);
        btnChatForum = findViewById(R.id.btnChatForum);
        btnAccessAIServices = findViewById(R.id.btnAccessAIServices);
        btnMakePayment = findViewById(R.id.btnMakePayment);

        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboardActivity.this, CheckOutActivity.class);
                startActivity(intent);
            }
        });

        btnAccessVetServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Schedule Appointment Activity
                startActivity(new Intent(UserDashboardActivity.this, VeterinaryServicesActivity.class));
            }
        });

        btnHealthRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboardActivity.this, UserAppointmentsActivity.class);
                startActivity(intent);
            }
        });

        btnChatForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboardActivity.this, ChatForumActivity.class);
                startActivity(intent);
            }
        });

        btnAccessAIServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserDashboardActivity.this, CattleDisease.class);
                startActivity(intent);
            }
        });


    }
}
