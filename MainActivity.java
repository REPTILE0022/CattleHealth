package com.example.cattlehealth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Called when "Proceed as User" button is clicked
    public void proceedAsUser(View view) {
        Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
        startActivity(intent);
    }

    // Called when "Proceed as Admin" button is clicked
    public void proceedAsAdmin(View view) {
        Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
        startActivity(intent);
    }
}
