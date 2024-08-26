package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ExerciseDetailActivity extends AppCompatActivity {

    private TextView textViewName, textViewDesc, textViewActivity;
    private ImageView imageViewExercise;
    private Button buttonNext;
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
        Exercise2 exercise = (Exercise2) intent.getSerializableExtra("exercise");
        currentPosition = intent.getIntExtra("currentPosition", -1);

        if (exercise != null) {
            displayExerciseDetails(exercise);
        }

        buttonNext.setOnClickListener(v -> {
            Intent nextIntent = new Intent(ExerciseDetailActivity.this, FocusBody.class);
            nextIntent.putExtra("currentPosition", currentPosition + 1); // Assuming next exercise is in the next position
            startActivity(nextIntent);
        });
    }

    private void displayExerciseDetails(Exercise2 exercise) {
        textViewName.setText(exercise.getExName());
        textViewDesc.setText(exercise.getExDesc());
        textViewActivity.setText("Activity: " + exercise.getActivity());

        Glide.with(this)
                .load(exercise.getImageUrl())
                .into(imageViewExercise);
    }
}
