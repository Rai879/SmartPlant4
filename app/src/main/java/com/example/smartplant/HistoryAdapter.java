package com.example.smartplant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private List<DatabaseHelper.HistoryItem> historyList;

    public HistoryAdapter(Context context, List<DatabaseHelper.HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return historyList.get(position).id;
    }

    // ... di dalam HistoryAdapter.java

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvType = convertView.findViewById(R.id.tvType);
            holder.tvPotSize = convertView.findViewById(R.id.tvPotSize);
            holder.tvLocation = convertView.findViewById(R.id.tvLocation);
            holder.tvTemperature = convertView.findViewById(R.id.tvTemperature);
            holder.tvWaterAmount = convertView.findViewById(R.id.tvWaterAmount);
            holder.tvTimestamp = convertView.findViewById(R.id.tvTimestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DatabaseHelper.HistoryItem item = historyList.get(position);

        holder.tvName.setText("Nama Tanaman: " + item.name);
        holder.tvType.setText("Tipe: " + item.type);
        holder.tvPotSize.setText("Ukuran Pot: " + item.potSize + " cm");
        holder.tvLocation.setText("Lokasi: " + item.location);
        holder.tvTemperature.setText(String.format("Suhu: %.1f °C", item.temperature));
        holder.tvWaterAmount.setText(String.format("Kebutuhan Air: %.2f Milliliter", item.waterAmount));
        holder.tvTimestamp.setText("Waktu: " + item.timestamp); // Anda mungkin ingin memformat timestamp ini lebih lanjut

        return convertView;
    }
// ...

    private static class ViewHolder {
        TextView tvName, tvType, tvPotSize, tvLocation, tvTemperature, tvWaterAmount, tvTimestamp;
    }
}
