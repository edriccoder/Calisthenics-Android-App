package com.example.gymapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.List;

public class exercise_adapter extends ArrayAdapter<Exercise> {
    private List<Exercise> exercises;
    private String username;
    private String exerciseDay;

    public exercise_adapter(Context context, List<Exercise> exercises, String username, String exerciseDay) {
        super(context, 0, exercises);
        this.exercises = new ArrayList<>(exercises);
        this.username = username;
        this.exerciseDay = exerciseDay;
    }

    // Set a new exercise day and notify the data change
    public void setExerciseDay(String exerciseDay) {
        this.exerciseDay = exerciseDay;
        notifyDataSetChanged();  // Notify that the data has changed
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exercise exercise = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView exerciseNameTextView = convertView.findViewById(R.id.exerciseNameTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView difficultyTextView = convertView.findViewById(R.id.difficultyTextView);
        Button addButton = convertView.findViewById(R.id.addButton);

        // Set exercise name
        exerciseNameTextView.setText(exercise.getExerciseName());

        // Load exercise image with Glide
        if (!exercise.getImageUrl().isEmpty()) {
            if (exercise.getImageUrl().endsWith(".gif")) {
                Glide.with(getContext()).asGif().load(exercise.getImageUrl()).into(imageView);
            } else {
                Glide.with(getContext())
                        .load(exercise.getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            }
        } else {
            imageView.setImageResource(R.drawable.dumbell); // Default image
        }

        // Set description and difficulty
        descriptionTextView.setText("Main Focus: " + exercise.getFocusbody());
        difficultyTextView.setText("Difficulty: " + exercise.getDifficulty());

        // Add button click listener to add exercise to personalized list
        addButton.setOnClickListener(v -> addExerciseToPersonalizedList(exercise));

        return convertView;
    }

    // Get number of exercises
    @Override
    public int getCount() {
        return exercises.size();
    }

    // Get specific exercise at a position
    @Override
    public Exercise getItem(int position) {
        return exercises.get(position);
    }

    // Get item ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Update the adapter with a filtered list of exercises
    public void filterList(ArrayList<Exercise> filteredList) {
        exercises.clear();
        exercises.addAll(filteredList);
        notifyDataSetChanged();  // Notify changes
    }

    // Add exercise to personalized list and send the data to the backend using PutData
    private void addExerciseToPersonalizedList(Exercise exercise) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            // Define the fields and data to send
            String[] field = new String[]{
                    "username", "exercise_day", "exercise_name", "exdesc", "eximg",
                    "exdifficulty", "focusbody", "activity_goal"
            };

            String[] data = new String[]{
                    username, exerciseDay, exercise.getExerciseName(),
                    exercise.getDescription(), exercise.getImageUrl(),
                    exercise.getDifficulty(), exercise.getFocusbody(),
                    exercise.getBuildMuscle()
            };

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insertPersonalize.php", "POST", field, data);

            // Start the request and handle the result
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    Log.d("AddExercise", "Exercise added successfully: " + result);
                } else {
                    String errorMessage = "Error adding exercise.";
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("AddExercise", errorMessage);
                }
            } else {
                String errorMessage = "Error starting request.";
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("AddExercise", errorMessage);
            }
        });
    }
}
