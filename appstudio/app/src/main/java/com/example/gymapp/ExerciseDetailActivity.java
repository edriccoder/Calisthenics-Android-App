package com.example.gymapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExerciseDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewDesc, textViewActivity;
    private ImageView imageViewExercise;
    private Button buttonNext, emgBut;
    private ArrayList<Exercise2> exerciseList;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        textViewName = findViewById(R.id.textViewDetailName);
        textViewDesc = findViewById(R.id.textViewDetailDesc);
        textViewActivity = findViewById(R.id.textViewDetailActivity);
        imageViewExercise = findViewById(R.id.imageViewDetailExercise);
        buttonNext = findViewById(R.id.buttonNext);
        emgBut = findViewById(R.id.emgBut);

        Intent intent = getIntent();
        exerciseList = (ArrayList<Exercise2>) intent.getSerializableExtra("exerciseList");
        currentPosition = intent.getIntExtra("currentPosition", -1);

        if (exerciseList != null && !exerciseList.isEmpty() && currentPosition >= 0 && currentPosition < exerciseList.size()) {
            displayExerciseDetails(exerciseList.get(currentPosition));
        } else {
            Toast.makeText(this, "Exercise data is not available", Toast.LENGTH_SHORT).show();
        }


        // Button to move to the next exercise
        buttonNext.setOnClickListener(v -> {
            if (currentPosition < exerciseList.size() - 1) {
                showRestingDialog();
            } else {
                Toast.makeText(ExerciseDetailActivity.this, "You have reached the last exercise.", Toast.LENGTH_SHORT).show();
            }
            updateExerciseCount();
            insertCalories();
        });

        emgBut.setOnClickListener(v -> {
            Intent intent1 = new Intent(ExerciseDetailActivity.this, emg_bluetooth.class);
            intent1.putExtra("hide_button", true); // Pass flag to hide button
            startActivity(intent1);
        });

    }

    private void displayExerciseDetails(Exercise2 exercise) {
        textViewName.setText(exercise.getExName() != null ? exercise.getExName() : "No name available");
        textViewDesc.setText(exercise.getExDesc() != null ? exercise.getExDesc() : "No description available");
        textViewActivity.setText("Activity: " + (exercise.getActivity() != null ? exercise.getActivity() : "Not specified"));

        // Load image with Glide
        if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(exercise.getImageUrl())
                    .into(imageViewExercise);
        } else {
            imageViewExercise.setImageResource(R.drawable.dumbell); // Fallback image
        }
    }

    private void showRestingDialog() {
        // Create the dialog for resting
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_rest);

        TextView textViewExerciseName = dialog.findViewById(R.id.textViewExerciseName);
        EditText editTextSets = dialog.findViewById(R.id.editTextSets);
        EditText editTextReps = dialog.findViewById(R.id.editTextReps);
        TextView textViewCountdown = dialog.findViewById(R.id.textViewCountdown);
        Button buttonNextExercise = dialog.findViewById(R.id.buttonNextExercise);
        Button buttonSkipExercise = dialog.findViewById(R.id.buttonSkipExercise); // Get reference to Skip button

        // Set the exercise name
        textViewExerciseName.setText(exerciseList.get(currentPosition).getExName());

        String name = exerciseList.get(currentPosition).getExName();

        // Variable to track remaining time
        final int[] remainingTime = {30}; // Start with 30 seconds

        // Create a 30-second countdown timer
        CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                remainingTime[0] = (int) (millisUntilFinished / 1000); // Store remaining time
                textViewCountdown.setText(String.valueOf(remainingTime[0])); // Update countdown
            }

            public void onFinish() {
                remainingTime[0] = 0; // Set remaining time to 0 when finished
                buttonNextExercise.setEnabled(true); // Enable the "Next Exercise" button
            }
        }.start();

        // Handle the "Next Exercise" button click (when timer finishes)
        buttonNextExercise.setOnClickListener(v -> {
            String sets = editTextSets.getText().toString();
            String reps = editTextReps.getText().toString();
            if (!sets.isEmpty() && !reps.isEmpty()) {
                logExerciseData(sets, reps, name); // Save sets and reps
                dialog.dismiss(); // Close the dialog
                currentPosition++;
                displayExerciseDetails(exerciseList.get(currentPosition));
                insertDurationToDatabase(0); // Insert remaining time to the database
            } else {
                Toast.makeText(ExerciseDetailActivity.this, "Please enter sets and reps", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle the "Skip" button click (when user skips manually)
        buttonSkipExercise.setOnClickListener(v -> {
            countDownTimer.cancel(); // Cancel the countdown timer
            dialog.dismiss(); // Close the dialog
            currentPosition++;
            if (currentPosition < exerciseList.size()) {
                displayExerciseDetails(exerciseList.get(currentPosition));
                insertDurationToDatabase(0); // Insert remaining time to the database
            } else {
                Toast.makeText(ExerciseDetailActivity.this, "You have reached the last exercise.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ExerciseDetailActivity.this, tracking.class);
                startActivity(intent);
            }
        });

        dialog.show(); // Show the dialog
    }


    private void insertDurationToDatabase(int remainingTime) {
        String username = MainActivity.GlobalsLogin.username;
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Create data to send to PHP script
        HashMap<String, String> data = new HashMap<>();
        data.put("username", username);
        data.put("time_seconds", String.valueOf(remainingTime));
        data.put("date", date);

        // Convert HashMap to arrays
        String[] keys = data.keySet().toArray(new String[0]);
        String[] values = data.values().toArray(new String[0]);

        PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insertZeroDuration.php", "POST", keys, values);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals("Success")) {
                    Toast.makeText(this, "Duration inserted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error inserting duration", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void logExerciseData(String sets, String reps, String name) {
        String url = "https://calestechsync.dermocura.net/calestechsync/update_exercise_log.php";

        String username = MainActivity.GlobalsLogin.username;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("ServerResponse", "Raw Response: " + response); // Log the raw response
                    try {
                        JSONObject jsonObject = new JSONObject(response); // Attempt to parse as JSON
                        if (jsonObject.has("success")) {
                            Toast.makeText(this, "Exercise log updated", Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.has("error")) {
                            Toast.makeText(this, "Error: " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ExerciseDetailActivity", "JSON parsing error: " + e.getMessage());
                        Log.e("ExerciseDetailActivity", "Response received: " + response); // Log the raw response
                        Toast.makeText(this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ExerciseDetailActivity", "Volley Error: " + error.getMessage());
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("exercise_name", name); // Ensure this is fetched correctly
                params.put("sets", sets);
                params.put("reps", reps);
                params.put("date", currentDate);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void updateExerciseCount() {
        String url = "https://calestechsync.dermocura.net/calestechsync/update_exercise_count.php";

        String username = MainActivity.GlobalsLogin.username;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success")) {
                            String successMessage = jsonObject.getString("success");
                            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();
                        } else if (jsonObject.has("error")) {
                            String errorMessage = jsonObject.getString("error");
                            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ExerciseDetailActivity", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log the error message to Logcat
                    Log.e("ExerciseDetailActivity", "Volley error: " + error.getMessage());
                    // Show a user-facing message via Toast (you can customize this message)
                    Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("date", currentDate);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void insertCalories() {
        // Get the username from the global session
        String username = MainActivity.GlobalsLogin.username;

        // Get the exercise name from the current exercise
        String exerciseName = exerciseList.get(currentPosition).getExName();

        // Get today's date in the format 'yyyy-MM-dd'
        // Get today's date in the format 'yyyy-MM-dd'
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        Log.d("DateInsert", "Current Date: " + currentDate);


        // URL for inserting calories burned
        String url = "https://calestechsync.dermocura.net/calestechsync/insertCaloriesBurned.php";

        // Create the POST request to send data to the server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Log the server's raw response
                    Log.d("ServerResponse", "Raw Response: " + response);

                    // Parse the response to get the required data (assuming JSON)
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getBoolean("success")) {
                            Log.d("CaloriesInsert", "Calories burned record created successfully.");

                            // Use getDouble and cast to float
                            float weight = (float) jsonResponse.getDouble("weight");
                            float metValue = (float) jsonResponse.getDouble("met_value");
                            int durationInSeconds = jsonResponse.getInt("duration_in_seconds");

                            // Log the values
                            Log.d("CaloriesInsert", "Weight: " + weight);
                            Log.d("CaloriesInsert", "MET Value: " + metValue);
                            Log.d("CaloriesInsert", "Duration in Seconds: " + durationInSeconds);
                        } else {
                            Log.e("CaloriesInsert", "Failed to create calories burned record: " + jsonResponse.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("CaloriesInsert", "JSON parsing error: " + e.getMessage());
                    }

                    // Display the server's response as a Toast message
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Enhanced error logging
                    if (error.networkResponse != null) {
                        // Log status code
                        int statusCode = error.networkResponse.statusCode;
                        Log.e("ServerError", "Status Code: " + statusCode);

                        // Log response data (if available)
                        String responseBody = "";
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Log.e("ServerError", "Response Body: " + responseBody);

                        // Optional: Display status code and error body to the user
                        Toast.makeText(this, "Error: " + statusCode + "\n" + responseBody, Toast.LENGTH_LONG).show();
                    } else {
                        // General error message if no network response
                        Log.e("ExerciseDetailActivity", "Volley Error: " + error.getMessage());
                        Toast.makeText(this, "Network error! Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("exercise_name", exerciseName);
                params.put("date", currentDate);  // Send today's date
                Log.d("DateParam", "Sent Date: " + currentDate);  // Log the date being sent
                return params;
            }
        };

// Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
