package com.example.smartplant;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherHelper {
    private static final String API_KEY = "d07373d427e96837bca892cfdcf182a7"; // Pastikan ini API key Anda yang benar
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String TAG = "WeatherHelper";

    // Gunakan RequestQueue singleton untuk efisiensi
    private static RequestQueue requestQueue;

    // Inisialisasi RequestQueue jika belum ada
    public static synchronized RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static void fetchTemperature(Context context, String cityName, TemperatureCallback callback) {
        // Encode city name for URL to handle spaces, etc.
        String encodedCityName;
        try {
            encodedCityName = java.net.URLEncoder.encode(cityName, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            Log.e(TAG, "City name encoding failed: " + e.getMessage());
            callback.onError("Failed to encode city name.");
            return;
        }

        String url = BASE_URL + "?q=" + encodedCityName + "&units=metric&appid=" + API_KEY;
        Log.d(TAG, "Requesting URL: " + url);

        // Gunakan RequestQueue yang sudah di-singleton
        RequestQueue queue = getRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temp = main.getDouble("temp");
                        Log.d(TAG, "Temperature fetched successfully: " + temp + "°C for " + cityName);
                        callback.onSuccess(temp);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error for " + cityName + ": " + e.getMessage());
                        callback.onError("Parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    String errorMessage = "Network or API error for " + cityName;
                    if (error != null) {
                        if (error.networkResponse != null) {
                            errorMessage += ". Status: " + error.networkResponse.statusCode;
                            try {
                                String responseBody = new String(error.networkResponse.data, "UTF-8");
                                errorMessage += ". Response Body: " + responseBody;
                            } catch (Exception e) {
                                // Ignore, just log the raw body if UTF-8 fails
                                errorMessage += ". Raw Response: " + new String(error.networkResponse.data);
                            }
                        } else {
                            if (error instanceof com.android.volley.NoConnectionError) {
                                errorMessage += ". No internet connection or host unreachable.";
                            } else if (error instanceof com.android.volley.TimeoutError) {
                                errorMessage += ". Request timed out.";
                            } else if (error.getMessage() != null) {
                                errorMessage += ". Volley error: " + error.getMessage();
                            } else {
                                errorMessage += ". Unknown Volley error.";
                            }
                        }
                    }
                    Log.e(TAG, "Volley error detailed: " + errorMessage);
                    callback.onError(errorMessage);
                });

        // Tambahkan permintaan ke antrean
        queue.add(jsonObjectRequest);
    }

    public interface TemperatureCallback {
        void onSuccess(double temperature);
        void onError(String errorMessage);
    }
}