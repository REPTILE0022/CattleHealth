package com.example.cattlehealth;

import static com.example.cattlehealth.Mpesa.Constants.BUSINESS_SHORT_CODE;
import static com.example.cattlehealth.Mpesa.Constants.CALLBACKURL;
import static com.example.cattlehealth.Mpesa.Constants.PARTYB;
import static com.example.cattlehealth.Mpesa.Constants.PASSKEY;
import static com.example.cattlehealth.Mpesa.Constants.TRANSACTION_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cattlehealth.Mpesa.AccessToken;
import com.example.cattlehealth.Mpesa.DarajaApiClient;
import com.example.cattlehealth.Mpesa.STKPush;
import com.example.cattlehealth.Mpesa.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutActivity extends AppCompatActivity {

    private EditText edtNumber, edtAmount;
    private ProgressBar mProgressBar;
    private Button btnPay;
    private DarajaApiClient mApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        edtNumber = findViewById(R.id.edtNumber);
        edtAmount = findViewById(R.id.edtAmount);
        btnPay = findViewById(R.id.btnPay);
        mProgressBar = findViewById(R.id.progressBar);

        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String transactionId = edtNumber.getText().toString().trim();
                if (transactionId.isEmpty() || transactionId.length() < 10 || transactionId.length() > 10 || !transactionId.startsWith("0")) {
                    edtNumber.setError("Correct Mpesa Paying Number required");
                    edtNumber.requestFocus();
                } else {
                    getAccessToken();
                }
            }
        });
    }

    private void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {
                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                    performSTKPush(edtNumber.getText().toString(), edtAmount.getText().toString());
                } else {
                    Toast.makeText(CheckOutActivity.this, "Failed to get access token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {
                Toast.makeText(CheckOutActivity.this, "Token retrieval failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSTKPush(String phoneNumber, String amount) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);

        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phoneNumber),
                PARTYB,
                Utils.sanitizePhoneNumber(phoneNumber),
                CALLBACKURL,
                "Cattle Health", // Account reference
                "Book an appointment now" // Transaction description
        );

        mApiClient.setGetAccessToken(false);
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                mProgressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(CheckOutActivity.this, "Payment made successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CheckOutActivity.this, UserDashboardActivity.class));
                    finish();
                } else {
                    edtNumber.setError("Enter Valid Phone Number");
                    Toast.makeText(CheckOutActivity.this, "Payment is not successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(CheckOutActivity.this, "Payment failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
