package com.example.smartplant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner; // Pastikan ini diimport
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // --- DEKLARASI VARIABEL YANG BENAR ---
    EditText etPlantName, etPotSize; // etPlantType sudah dihapus, spinnerPlantType adalah Spinner
    Spinner spinnerPlantType; // Deklarasikan sebagai Spinner
    Spinner spinnerLocation;
    Button btnCalculate, btnHistory;
    DatabaseHelper dbHelper;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etPlantName = findViewById(R.id.etPlantName); // Pastikan ini juga ada di layout
        spinnerPlantType = findViewById(R.id.spinnerPlantType); // Inisialisasi sebagai Spinner
        etPotSize = findViewById(R.id.etPotSize);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnHistory = findViewById(R.id.btnHistory);

        dbHelper = new DatabaseHelper(this);

        String[] locations = {
                "Lokasi Saat Ini",
                "Jakarta",
                "Surabaya",
                "Bandung",
                "Yogyakarta",
                "Medan",
                "Semarang",
                "Makassar",
                "Palembang",
                "Denpasar",
                "Batam",
                "Padang",
                "Pekanbaru",
                "Malang",
                "Samarinda",
                "Banjarmasin",
                "Manado",
                "Pontianak",
                "Balikpapan",
                "Bandar Lampung",
                "Bogor",
                "Bekasi"
        };
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        spinnerLocation.setAdapter(locationAdapter);

        String[] plantTypes = {"Pilih Tipe Tanaman", "Anorganik", "Organik"};
        ArrayAdapter<String> plantTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, plantTypes);
        spinnerPlantType.setAdapter(plantTypeAdapter);

        if (btnCalculate != null) {
            btnCalculate.setOnClickListener(v -> {
                Log.d(TAG, "Tombol Hitung ditekan.");
                String name = etPlantName.getText().toString().trim();
                String type = spinnerPlantType.getSelectedItem().toString(); // Ambil dari spinner
                String potSizeStr = etPotSize.getText().toString().trim();
                String location = spinnerLocation.getSelectedItem().toString();

                if (name.isEmpty() || potSizeStr.isEmpty() || type.equals("Lain-lain")) {
                    Toast.makeText(this, "Mohon isi semua data dan pilih tipe tanaman yang valid", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Input tidak lengkap atau tipe tidak valid.");
                    return;
                }

                int potSize;
                try {
                    potSize = Integer.parseInt(potSizeStr);
                    Log.d(TAG, "Ukuran pot parsed: " + potSize);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Ukuran pot harus angka", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "NumberFormatException for pot size: " + e.getMessage());
                    return;
                }

                String city = location.equals("Lokasi Saat Ini") ? "Jakarta" : location;
                Log.d(TAG, "Mulai fetch temperature untuk kota: " + city);

                WeatherHelper.fetchTemperature(MainActivity.this, city, new WeatherHelper.TemperatureCallback() {
                    @Override
                    public void onSuccess(double temperature) {
                        Log.d(TAG, "Fetch temperature berhasil: " + temperature);
                        double waterAmount = calculateWater(type, potSize, temperature);

                        dbHelper.insertHistory(name, type, potSize, city, temperature, waterAmount);
                        Log.d(TAG, "Data riwayat disimpan.");

                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        intent.putExtra("result", waterAmount);
                        intent.putExtra("location", city);
                        intent.putExtra("temperature", temperature);
                        startActivity(intent);
                        Log.d(TAG, "Membuka ResultActivity.");
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Gagal ambil suhu: " + errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Fetch temperature gagal: " + errorMessage);
                    }
                });
            });
        } else {
            Log.e(TAG, "ERROR: btnCalculate tidak ditemukan di layout!");
            Toast.makeText(this, "Kesalahan Aplikasi: Tombol Hitung tidak ditemukan.", Toast.LENGTH_LONG).show();
        }

        if (btnHistory != null) {
            btnHistory.setOnClickListener(v -> {
                Log.d(TAG, "Tombol Riwayat ditekan.");
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e(TAG, "ERROR: btnHistory tidak ditemukan di layout!");
            Toast.makeText(this, "Kesalahan Aplikasi: Tombol Riwayat tidak ditemukan.", Toast.LENGTH_LONG).show();
        }
    }

    private double calculateWater(String type, int potSize, double temperature) {
        double base = potSize * 0.5;
        Log.d(TAG, "Base water: " + base);

        if (type.equalsIgnoreCase("Anorganik")) {
            base *= 0.5;
            Log.d(TAG, "Water after Anorganik adjustment: " + base);
        } else if (type.equalsIgnoreCase("Organik")) {
            base *= 1.2;
            Log.d(TAG, "Water after Organik adjustment: " + base);
        }

        if (temperature >= 30) {
            base *= 1.3;
            Log.d(TAG, "Water after hot temperature adjustment: " + base);
        } else if (temperature <= 15) {
            base *= 0.8;
            Log.d(TAG, "Water after cold temperature adjustment: " + base);
        }

        Log.d(TAG, "Final calculated water: " + base);
        return base;
    }
}