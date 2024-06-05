package com.example.gymapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class exercise_details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_details);

        String exerciseName = getIntent().getStringExtra("exerciseName");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String difficulty = getIntent().getStringExtra("difficulty");

        TextView exerciseNameTextView = findViewById(R.id.exerciseNameTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        ImageView imageView = findViewById(R.id.imageView);
        TextView difficultyTextView = findViewById(R.id.difficultyTextView);

        exerciseNameTextView.setText(exerciseName);
        descriptionTextView.setText(description);
        Glide.with(this).load(imageUrl).into(imageView);
        difficultyTextView.setText("Difficulty: " + difficulty);

    }
}