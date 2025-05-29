package com.example.smartplant;

import android.content.Intent;   // Tambahkan baris ini
import android.widget.Button;     // Tambahkan baris ini
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView tvWaterAmountResult;
    TextView tvLocationTemp;
    Button btnBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvWaterAmountResult = findViewById(R.id.tvWaterAmountResult);
        tvLocationTemp = findViewById(R.id.tvLocationTemp);
        btnBackToMain = findViewById(R.id.btnBackToMain);

        Intent intent = getIntent();
        if (intent != null) {
            double waterAmount = intent.getDoubleExtra("result", 0.0);
            String location = intent.getStringExtra("location");
            double temperature = intent.getDoubleExtra("temperature", 0.0);

            // TextView
            tvWaterAmountResult.setText(String.format("%.2f Milliliter/hari", waterAmount));
            tvLocationTemp.setText(String.format("Lokasi: %s, Suhu: %.1f °C", location, temperature));
        }

        btnBackToMain.setOnClickListener(v -> finish());
    }
}
