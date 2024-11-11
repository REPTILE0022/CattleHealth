package com.example.cattlehealth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RecommendationsActivity extends AppCompatActivity {

    private TextView diagnosisTextView;
    private TextView treatmentTextView;
    private TextView bestPracticesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        diagnosisTextView = findViewById(R.id.diagnosisTextView);
        treatmentTextView = findViewById(R.id.treatmentTextView);
        bestPracticesTextView = findViewById(R.id.bestPracticesTextView);

        String diagnosis = getIntent().getStringExtra("diagnosis");
        diagnosisTextView.setText("Diagnosis: " + diagnosis);

        if ("Healthy".equals(diagnosis)) {
            treatmentTextView.setText("Treatment: No treatment needed.");
            bestPracticesTextView.setText("Best Practices:\n" +
                    "- Maintain a balanced diet\n" +
                    "- Ensure access to clean water\n" +
                    "- Provide regular exercise\n" +
                    "- Schedule routine check-ups with a veterinarian\n" +
                    "- Keep living areas clean and hygienic");
        } else if ("Lumpy Skin Disease".equals(diagnosis)) {
            treatmentTextView.setText("Treatment:\n" +
                    "- Isolate affected animals\n" +
                    "- Provide supportive care (fluids, anti-inflammatory drugs)\n" +
                    "- Administer antibiotics to prevent secondary infections\n" +
                    "- Apply insect repellents to reduce vector transmission");
            bestPracticesTextView.setText("Best Practices:\n" +
                    "- Implement strict biosecurity measures\n" +
                    "- Vaccinate healthy animals in the herd\n" +
                    "- Control insect vectors (flies, mosquitoes)\n" +
                    "- Regularly inspect animals for early signs\n" +
                    "- Consult with a veterinarian for a comprehensive management plan");
        }
    }
}