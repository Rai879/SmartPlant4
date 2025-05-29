package com.example.smartplant;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    ListView listViewHistory;
    DatabaseHelper dbHelper;
    HistoryAdapter adapter;
    List<DatabaseHelper.HistoryItem> historyList;

    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        dbHelper = new DatabaseHelper(this);
        loadHistoryData();

        listViewHistory.setOnItemLongClickListener((parent, view, position, id) -> {
            // Dapatkan item yang di-long-click
            DatabaseHelper.HistoryItem selectedItem = historyList.get(position);
            Log.d(TAG, "Item di-long-click: " + selectedItem.name + " (ID: " + selectedItem.id + ")");

            // dialog konfirmasi
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Riwayat")
                    .setMessage("Apakah Anda yakin ingin menghapus riwayat ini:\n" + selectedItem.name + " (" + selectedItem.timestamp + ")?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        // Hapus item dari database
                        int rowsAffected = dbHelper.deleteHistoryItem(selectedItem.id);
                        if (rowsAffected > 0) {
                            Toast.makeText(HistoryActivity.this, "Riwayat berhasil dihapus.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Riwayat berhasil dihapus dari DB: ID " + selectedItem.id);
                            // Muat ulang data untuk memperbarui tampilan ListView
                            loadHistoryData();
                        } else {
                            Toast.makeText(HistoryActivity.this, "Gagal menghapus riwayat.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Gagal menghapus riwayat dari DB: ID " + selectedItem.id);
                        }
                    })
                    .setNegativeButton("Batal", (dialog, which) -> {
                        dialog.dismiss(); // Tutup dialog
                    })
                    .show();
            return true;
        });
    }

    // data riwayat
    private void loadHistoryData() {
        historyList = dbHelper.getAllHistory(); // Muat data dari DB
        adapter = new HistoryAdapter(this, historyList);
        listViewHistory.setAdapter(adapter);
        Log.d(TAG, "Data riwayat dimuat ulang. Jumlah item: " + historyList.size());

        if (historyList.isEmpty()) {
            Toast.makeText(this, "Belum ada riwayat perhitungan.", Toast.LENGTH_LONG).show();
        }
    }
}