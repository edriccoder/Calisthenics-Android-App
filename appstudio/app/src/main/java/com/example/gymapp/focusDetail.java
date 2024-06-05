package com.example.gymapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.List;

public class focusDetail extends AppCompatActivity {

    private ImageView exerciseImageView;
    private TextView exerciseNameTextView;
    private TextView exerciseDescriptionTextView;
    private TextView exerciseDifficultyTextView;
    private TextView exerciseActivityTextView;
    private Button nextButton;
    private ImageButton play;
    private VideoView videoView;
    private LinearLayout videoLayout;

    private List<focusExercise> exerciseList;
    private int currentIndex;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_detail);

        exerciseImageView = findViewById(R.id.exerciseImageView);
        exerciseNameTextView = findViewById(R.id.exerciseNameTextView);
        exerciseDescriptionTextView = findViewById(R.id.exerciseDescriptionTextView);
        exerciseDifficultyTextView = findViewById(R.id.exerciseDifficultyTextView);
        exerciseActivityTextView = findViewById(R.id.exerciseActivityTextView);
        nextButton = findViewById(R.id.nextButton);
        play = findViewById(R.id.play);
        videoView = findViewById(R.id.videoView);
        videoLayout = findViewById(R.id.videoLayout);

        exerciseList = getIntent().getParcelableArrayListExtra("exerciseList");
        currentIndex = getIntent().getIntExtra("currentIndex", 0);
        username = getIntent().getStringExtra("username");

        displayExerciseDetails(currentIndex);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.setVideoURI(Uri.parse("https://calestechsync.dermocura.net/calestechsync/eximg/emg_pushup.mp4"));
                videoView.start();
                videoLayout.setVisibility(View.VISIBLE);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex + 1) % exerciseList.size();
                displayExerciseDetails(currentIndex);

                String exname = exerciseNameTextView.getText().toString();
                String eximg = exerciseList.get(currentIndex).getImageUrl();

                String[] exerciseFields = {"username", "exname", "eximg"};
                String[] exerciseData = {username, exname, eximg};

                // Make the PutData request
                PutData putExerciseData = new PutData("https://calestechsync.dermocura.net/calestechsync/trackingExercise.php", "POST", exerciseFields, exerciseData);
                putExerciseData.startPut();

                // Check if the request is complete and handle the result
                if (putExerciseData.onComplete()) {
                    String result = putExerciseData.getResult();
                    Toast.makeText(focusDetail.this, result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Exercise created successfully")) {
                        // Exercise creation was successful, perform any additional actions here
                    }
                } else {
                    Toast.makeText(focusDetail.this, "Error creating exercise", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exerciseDescriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void displayExerciseDetails(int index) {
        focusExercise exercise = exerciseList.get(index);

        exerciseNameTextView.setText(exercise.getExerciseName());
        exerciseDescriptionTextView.setText(exercise.getDescription());
        exerciseDifficultyTextView.setText("Difficulty: " + exercise.getDifficulty());
        exerciseActivityTextView.setText("Activity: " + exercise.getActivity());

        if (!exercise.getImageUrl().isEmpty()) {
            if (exercise.getImageUrl().endsWith(".gif")) {
                Glide.with(this).asGif().load(exercise.getImageUrl()).into(exerciseImageView);
            } else {
                Glide.with(this)
                        .load(exercise.getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(exerciseImageView);
            }
        } else {
            exerciseImageView.setImageResource(R.drawable.dumbell);
        }

        // Hide the video view layout when displaying new exercise details
        videoLayout.setVisibility(View.GONE);
        videoView.stopPlayback();
    }
}
