package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONException;
import org.json.JSONObject;

public class generate_week extends AppCompatActivity {
    Button done;
    ProgressBar progressBar;
    TextView progressNumber;
    private static final String TAG = "GenerateWeek"; // Define a tag for logging
    private Handler handler = new Handler(); // Handler for progress updates
    private boolean isProgressComplete = false; // Flag to check if progress has reached 100%

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_week);

        String username = signups.Globals.username;
        done = findViewById(R.id.done);
        progressBar = findViewById(R.id.progress_circular);
        progressNumber = findViewById(R.id.progress_number);

        // Initially disable the button
        done.setEnabled(false);

        // Automatically start loading when the activity starts
        generateExercisePlan(username);

        // Set the click listener for the button
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the progress is complete before moving to the next activity
                if (isProgressComplete) {
                    Intent intent = new Intent(generate_week.this, MainActivity.class); // Replace MainActivity.class with your target activity
                    startActivity(intent);
                    finish(); // Optionally close the current activity
                } else {
                    Toast.makeText(generate_week.this, "Please wait, still generating...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateExercisePlan(String username) {
        // Disable the button while loading
        done.setEnabled(false);
        progressBar.setProgress(0);
        progressNumber.setText("0%");

        // Fields and data to be sent via POST request
        String[] field = new String[1];
        field[0] = "username";

        String[] data = new String[1];
        data[0] = username;

        // Create PutData object for the POST request
        PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/generate_weekly.php", "POST", field, data);
        if (putData.startPut()) {
            // Simulate progress over 5 seconds
            updateProgressBar();  // 5000ms = 5 seconds

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (putData.onComplete()) {
                        // Task is complete, handle the result
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Get the result from the server
                                String result = putData.getResult();
                                Toast.makeText(generate_week.this, result, Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "Server Response: " + result); // Log the server response

                                // Parse the JSON response
                                try {
                                    JSONObject jsonResponse = new JSONObject(result);
                                    String message = jsonResponse.getString("message");

                                    if (message.equals("Weekly plan generated successfully")) {
                                        // Plan generated, enable the button
                                        done.setEnabled(true);
                                    } else {
                                        Log.e(TAG, "Failed to generate weekly plan: " + message); // Log the error
                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                                }
                            }
                        });
                    }
                }
            }).start();
        } else {
            Log.e(TAG, "Failed to start PutData request."); // Log if startPut fails
            Toast.makeText(generate_week.this, "Failed to send data!", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to update the progress bar over time
    private void updateProgressBar() {
        final int progressMax = 100;
        final int interval = 100; // Update every 100ms
        final int steps = 5000 / interval; // Number of steps to reach 100%

        handler.post(new Runnable() {
            int progress = 0;

            @Override
            public void run() {
                if (progress <= progressMax) {
                    progressBar.setProgress(progress);
                    progressNumber.setText(progress + "%");

                    // Increment progress
                    progress += (progressMax / steps);

                    // If progress is below 100, schedule the next update
                    if (progress <= progressMax) {
                        handler.postDelayed(this, interval);
                    } else {
                        // When progress is complete (100%), set the flag and enable the button
                        isProgressComplete = true;
                        progressBar.setProgress(100);
                        progressNumber.setText("100%");
                        done.setEnabled(true); // Enable the button to proceed to the next activity
                    }
                }
            }
        });
    }
}