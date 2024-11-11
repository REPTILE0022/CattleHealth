package com.example.cattlehealth;// AdminDashboardActivity.java
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private Button btnViewAppointments;
    private Button btnChatUpdateForum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnViewAppointments = findViewById(R.id.btnViewAppointments);

        btnChatUpdateForum = findViewById(R.id.btnChatUpdateForum);

        btnViewAppointments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ViewAppointmentsActivity.class);
                startActivity(intent);
            }
        });
        btnChatUpdateForum.setOnClickListener(view -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminChatForumActivity.class);
            startActivity(intent);
        });
    }
}
