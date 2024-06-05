package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ExerciseDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_details2);

        ImageView detailsImg = findViewById(R.id.details_img);
        TextView detailsName = findViewById(R.id.details_name);
        TextView detailsDesc = findViewById(R.id.details_desc);
        TextView detailsDifficulty = findViewById(R.id.details_difficulty);
        TextView detailsActivity = findViewById(R.id.details_activity);

        Intent intent = getIntent();
        String exerciseName = intent.getStringExtra("exerciseName");
        String activity = intent.getStringExtra("activity");
        String difficulty = intent.getStringExtra("difficulty");
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("imageUrl");

        detailsName.setText(exerciseName);
        detailsActivity.setText(activity);
        if (difficulty != null) {
            detailsDifficulty.setText("Difficulty: " + difficulty); // Set the difficulty text
        }

        if (description != null) {
            detailsDesc.setText(description); // Set the description text
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.endsWith(".gif")) {
                Glide.with(this).asGif().load(imageUrl).into(detailsImg); // Load gif image
            } else {
                Glide.with(this)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(detailsImg); // Load normal image
            }
        } else {
            detailsImg.setImageResource(R.drawable.abs); // Set default image if imageUrl is empty
        }
    }
}
