package com.example.gymapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

        TextView textViewCountdown = dialog.findViewById(R.id.textViewCountdown);
        Button buttonNextExercise = dialog.findViewById(R.id.buttonNextExercise);

        // Create a 30-second countdown timer
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the countdown text
                textViewCountdown.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                // Enable the "Next Exercise" button when the timer finishes
                buttonNextExercise.setEnabled(true);
            }
        }.start();

        // Handle the "Next Exercise" button click
        buttonNextExercise.setOnClickListener(v -> {
            dialog.dismiss(); // Close the dialog
            currentPosition++;
            displayExerciseDetails(exerciseList.get(currentPosition)); // Show the next exercise
        });

        dialog.show(); // Show the dialog
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
}
