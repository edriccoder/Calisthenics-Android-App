package com.example.gymapp;

import android.os.Bundle;
import android.util.Log; // For logging
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class personalize extends AppCompatActivity {

    private Spinner spinnerMonday, spinnerTuesday, spinnerWednesday, spinnerThursday, spinnerFriday;
    private static final String TAG = "PersonalizeActivity"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize);

        Button done = findViewById(R.id.done);
        spinnerMonday = findViewById(R.id.exerciseMonday);
        spinnerTuesday = findViewById(R.id.exerciseTuesday);
        spinnerWednesday = findViewById(R.id.exerciseWednesday);
        spinnerThursday = findViewById(R.id.exerciseThursday);
        spinnerFriday = findViewById(R.id.exerciseFriday);
        Spinner spinnerSaturday = findViewById(R.id.exerciseSaturday); // New Spinner
        Spinner spinnerSunday = findViewById(R.id.exerciseSunday); // New Spinner

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect weekly plan data from spinners
                Map<String, String> weeklyPlan = new HashMap<>();
                weeklyPlan.put("Monday", spinnerMonday.getSelectedItem().toString());
                weeklyPlan.put("Tuesday", spinnerTuesday.getSelectedItem().toString());
                weeklyPlan.put("Wednesday", spinnerWednesday.getSelectedItem().toString());
                weeklyPlan.put("Thursday", spinnerThursday.getSelectedItem().toString());
                weeklyPlan.put("Friday", spinnerFriday.getSelectedItem().toString());
                weeklyPlan.put("Saturday", spinnerSaturday.getSelectedItem().toString()); // New entry
                weeklyPlan.put("Sunday", spinnerSunday.getSelectedItem().toString()); // New entry

                JSONObject weeklyPlanJson = new JSONObject(weeklyPlan);
                String weeklyPlanString = weeklyPlanJson.toString();

                // Create a new thread for each request
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Fields to send in the POST request
                        String username = signups.Globals.username;
                        String[] field = {"username", "weeklyPlan"};
                        String[] data = {username, weeklyPlanString};

                        PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insert_generateWeekly.php", "POST", field, data);

                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();

                                // Log the raw server response for debugging
                                Log.d(TAG, "Server response: " + result);

                                // Handle the result from the server
                                try {
                                    JSONObject jsonResponse = new JSONObject(result);
                                    String status = jsonResponse.getString("status");
                                    String message = jsonResponse.getString("message");

                                    runOnUiThread(() -> {
                                        Toast.makeText(personalize.this, message, Toast.LENGTH_LONG).show();
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "JSON Parsing Error: " + e.getMessage());

                                    runOnUiThread(() -> Toast.makeText(personalize.this, "Error parsing response: " + result, Toast.LENGTH_LONG).show());
                                }
                            }
                        } else {
                            Log.e(TAG, "PutData startPut() failed");
                        }
                    }
                }).start(); // Start the new thread here
            }
        });
    }
}
