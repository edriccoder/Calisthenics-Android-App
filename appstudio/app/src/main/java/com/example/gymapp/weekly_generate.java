package com.example.gymapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class weekly_generate extends AppCompatActivity {

    private ListView listViewExercises;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise2> exerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_generate);

        listViewExercises = findViewById(R.id.listViewExercises);
        exerciseList = new ArrayList<>();
        Button buttonStartFirstExercise = findViewById(R.id.buttonStartFirstExercise);
        adapter = new ExerciseAdapter(this, exerciseList); // Initialize the adapter
        listViewExercises.setAdapter(adapter); // Set the adapter to ListView
        Button emgBut = findViewById(R.id.emgBut2);

        String username = MainActivity.GlobalsLogin.username;
        if (username == null || username.isEmpty()) {
            Log.e("Username Error", "Username is null or empty");
            Toast.makeText(this, "Username is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("Username", "Username: " + username);

        String count = getIntent().getStringExtra("count");
        if (count == null || count.isEmpty()) {
            Log.e("Count Error", "Exercise day (count) is null or empty");
            Toast.makeText(this, "Exercise day is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("WeeklyGenerateActivity", "Count: " + count);

        buttonStartFirstExercise.setOnClickListener(v -> {
            if (!exerciseList.isEmpty()) {
                // Start the ExerciseDetailActivity with the first item in the list
                Intent intent = new Intent(weekly_generate.this, ExerciseDetailActivity.class);
                intent.putExtra("exerciseList", exerciseList);  // Pass the full list of exercises
                intent.putExtra("currentPosition", 0);  // Start with the first exercise
                startActivity(intent);
            } else {
                Toast.makeText(weekly_generate.this, "No exercises available to start.", Toast.LENGTH_SHORT).show();
            }
        });

        emgBut.setOnClickListener(v -> {
            Intent intent1 = new Intent(weekly_generate.this, emg_bluetooth.class);
            startActivity(intent1);
        });

        fetchExercises(username, count);
    }

    private void fetchExercises(String username, String count) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[2]; // Changed size to 2
            field[0] = "username";
            field[1] = "exercise_day";

            String[] data = new String[2]; // Changed size to 2
            data[0] = username;
            data[1] = count;

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_weeklyGoal.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d("FetchExercises", "Full Response: " + result);
                    parseExercises(result);
                } else {
                    Toast.makeText(weekly_generate.this, "Error: Could not complete the request.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void parseExercises(String jsonData) {
        Log.d("ParseExercises", "JSON Data: " + jsonData);

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                JSONArray exercisesArray = jsonObject.getJSONArray("data");

                exerciseList.clear(); // Clear the existing list
                for (int i = 0; i < exercisesArray.length(); i++) {
                    JSONObject exerciseObject = exercisesArray.getJSONObject(i);

                    // Correct the field names according to the server response
                    String exName = exerciseObject.getString("exercise_name"); // Renamed to match server field
                    String exDesc = exerciseObject.getString("exdesc");
                    String exImg = exerciseObject.getString("eximg");
                    String activityGoal = exerciseObject.optString("activity_goal", "N/A");
                    String otherFocus = exerciseObject.optString("other_focus", "No focus specified");

                    // Add logic to download and save the image
                    Exercise2 exercise = new Exercise2(exName, exDesc, exImg, activityGoal, otherFocus);
                    downloadAndSaveImage(exercise); // Download and save the image
                    exerciseList.add(exercise);
                }

                runOnUiThread(() -> adapter.notifyDataSetChanged()); // Notify the adapter
            } else {
                Log.d("ParseExercises", "Error: No exercises found.");
                Toast.makeText(weekly_generate.this, "No exercises found.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(weekly_generate.this, "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void downloadAndSaveImage(Exercise2 exercise) {
        String imageUrl = exercise.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            saveImageToStorage(resource, exercise);
                        }
                    });
        }
    }

    private void saveImageToStorage(Bitmap bitmap, Exercise2 exercise) {
        File directory = new File(getExternalFilesDir(null), "MyAppImages");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, exercise.getExName() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            exercise.setLocalImagePath(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("SaveImage", "Error saving image: " + e.getMessage());
        }
    }
}
