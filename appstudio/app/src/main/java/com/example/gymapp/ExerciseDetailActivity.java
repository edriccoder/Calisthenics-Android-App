package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
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
    private Button buttonNext;
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

        // Get the intent data
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
                currentPosition++;
                displayExerciseDetails(exerciseList.get(currentPosition));
            } else {
                Toast.makeText(ExerciseDetailActivity.this, "You have reached the last exercise.", Toast.LENGTH_SHORT).show();
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
}
