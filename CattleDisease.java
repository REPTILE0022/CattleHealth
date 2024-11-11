package com.example.cattlehealth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cattlehealth.ml.CattleModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CattleDisease extends AppCompatActivity {

    private static final String TAG = "CattleDisease";
    private static final float CONFIDENCE_THRESHOLD = 0.90f;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1 MB

    private ImageView imageView;
    private TextView resultTextView;
    private Button uploadButton, predictButton, recommendationButton;
    private ProgressBar progressBar;
    private Bitmap imageBitmap;
    private CattleModel model;
    private String diagnosis;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cattle_disease);

        initializeViews();
        setupButtonListeners();
        setupImagePicker();
        setupExecutorService();
        checkAndRequestPermissions();
    }

    private void initializeViews() {
        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        uploadButton = findViewById(R.id.uploadButton);
        predictButton = findViewById(R.id.predictButton);
        recommendationButton = findViewById(R.id.recommendationButton);
        progressBar = findViewById(R.id.progressBar);

        predictButton.setEnabled(false);
        recommendationButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setupButtonListeners() {
        uploadButton.setOnClickListener(v -> pickImage());
        predictButton.setOnClickListener(v -> predictDisease());
        recommendationButton.setOnClickListener(v -> openRecommendationsActivity());
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            try {
                                imageBitmap = MediaStore.Images.Media.getBitmap(CattleDisease.this.getContentResolver(), uri);
                                imageBitmap = compressImage(imageBitmap);
                                imageView.setImageBitmap(imageBitmap);
                                predictButton.setEnabled(true);
                                resultTextView.setText("Image loaded successfully");
                                recommendationButton.setVisibility(View.GONE);
                            } catch (IOException e) {
                                Log.e(TAG, "Error loading image", e);
                                showErrorToast("Error loading image");
                            }
                        }
                    }
                });
    }

    private void setupExecutorService() {
        executorService = Executors.newSingleThreadExecutor();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }
    }

    private void pickImage() {
        pickImageLauncher.launch("image/*");
    }

    private void predictDisease() {
        if (imageBitmap != null) {
            setUIForPrediction(true);

            executorService.execute(() -> {
                try {
                    performPrediction();
                } catch (Exception e) {
                    handlePredictionError(e);
                } finally {
                    if (model != null) {
                        model.close();
                    }
                }
            });
        } else {
            showErrorToast("Please upload an image first");
        }
    }

    private void performPrediction() throws IOException {
        model = CattleModel.newInstance(CattleDisease.this);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 256, 256, true);
        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(resizedBitmap);
        ByteBuffer byteBuffer = tensorImage.getBuffer();

        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);
        inputFeature0.loadBuffer(byteBuffer);

        CattleModel.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

        float[] confidences = outputFeature0.getFloatArray();
        int maxPos = 0;
        float maxConfidence = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i];
                maxPos = i;
            }
        }

        String[] classes = {"Healthy", "Lumpy Skin Disease"};
        diagnosis = classes[maxPos];

        float finalMaxConfidence = maxConfidence;
        runOnUiThread(() -> updateUIAfterPrediction(finalMaxConfidence));
    }

    private void updateUIAfterPrediction(float confidence) {
        setUIForPrediction(false);
        if (confidence >= CONFIDENCE_THRESHOLD) {
            resultTextView.setText("Diagnosis: " + diagnosis);
            recommendationButton.setVisibility(View.VISIBLE);
        } else {
            resultTextView.setText("Diagnosis inconclusive. Please try with a clearer image.");
            recommendationButton.setVisibility(View.GONE);
        }
    }

    private void setUIForPrediction(boolean isPredicting) {
        predictButton.setEnabled(!isPredicting);
        uploadButton.setEnabled(!isPredicting);
        progressBar.setVisibility(isPredicting ? View.VISIBLE : View.GONE);
        resultTextView.setText(isPredicting ? "Processing..." : "");
        recommendationButton.setVisibility(View.GONE);
    }

    private void openRecommendationsActivity() {
        Intent intent = new Intent(CattleDisease.this, RecommendationsActivity.class);
        intent.putExtra("diagnosis", diagnosis);
        startActivity(intent);
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                showErrorToast("Storage permission denied. Cannot access images.");
            }
        }
    }

    private Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length > MAX_IMAGE_SIZE && options > 0) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        byte[] data = baos.toByteArray();
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private void handlePredictionError(Exception e) {
        runOnUiThread(() -> {
            setUIForPrediction(false);
            resultTextView.setText("Error predicting disease: " + e.getMessage());
            Log.e(TAG, "Error predicting disease", e);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
        if (model != null) {
            model.close();
        }
    }
}