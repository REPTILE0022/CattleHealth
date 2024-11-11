package com.example.cattlehealth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private static final String TAG = "AdminLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(AdminLoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Check if the user is an admin
                                if (isAdmin(user)) {
                                    // Admin login successful, navigate to admin dashboard
                                    startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                                    finish();
                                } else {
                                    // Not an admin, show error message
                                    Toast.makeText(AdminLoginActivity.this, "You are not authorized as admin", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            }
                        } else {
                            Log.e(TAG, "Admin login failed", task.getException());
                            Toast.makeText(AdminLoginActivity.this, "Admin login failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private boolean isAdmin(FirebaseUser user) {
        // Add logic to determine if the user is an admin based on your requirements
        // For example, you can check the email or UID of the user
        // Return true if the user is an admin, otherwise return false
        // Replace the placeholder condition with your actual admin check logic
        return user.getEmail() != null && user.getEmail().equals("admin@gmail.com");
    }
}
