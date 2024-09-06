package com.example.gymapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

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
        });

        emgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseDetailActivity.this, emg_bluetooth.class);
                startActivity(intent);
            }
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
}
