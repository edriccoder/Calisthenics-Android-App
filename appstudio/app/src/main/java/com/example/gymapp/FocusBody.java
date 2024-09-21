package com.example.gymapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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

public class FocusBody extends AppCompatActivity {

    private ExerciseAdapter adapter;
    private ArrayList<Exercise2> exercises;
    private Button emgBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_body);

        TextView textViewFocusBody = findViewById(R.id.textViewFocusBody);
        TextView textViewExDifficulty = findViewById(R.id.textViewExDifficulty);
        Button buttonStartFirstExercise = findViewById(R.id.buttonStartFirstExercise);
        ListView listViewExercises = findViewById(R.id.listViewExercises);

        exercises = new ArrayList<>();
        adapter = new ExerciseAdapter(this, exercises);
        listViewExercises.setAdapter(adapter);
        emgBut = findViewById(R.id.emgBut2);

        String focusBody = getIntent().getStringExtra("focusbody");
        String exDifficulty = getIntent().getStringExtra("exdifficulty");

        textViewFocusBody.setText("Focus: " + focusBody);
        textViewExDifficulty.setText("Difficulty: " + exDifficulty);

        fetchExercises(focusBody, exDifficulty);

        // Handle the button click to start the first exercise in the list
        buttonStartFirstExercise.setOnClickListener(v -> {
            if (!exercises.isEmpty()) {
                // Start the ExerciseDetailActivity with the first item in the list
                Intent intent = new Intent(FocusBody.this, ExerciseDetailActivity.class);
                intent.putExtra("exerciseList", exercises);  // Pass the full list of exercises
                intent.putExtra("currentPosition", 0);  // Start with the first exercise
                startActivity(intent);
            } else {
                Toast.makeText(FocusBody.this, "No exercises available to start.", Toast.LENGTH_SHORT).show();
            }
        });

        // Change the focus to pass the raw value
        emgBut.setOnClickListener(v -> {
            Intent intent1 = new Intent(FocusBody.this, emg_bluetooth.class);
            intent1.putExtra("focus", focusBody);  // Pass the raw focusBody string
            startActivity(intent1);
        });

    }


    private void fetchExercises(String focusBody, String exDifficulty) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;

            String[] field = new String[3];
            field[0] = "username";
            field[1] = "focusbody";
            field[2] = "exdifficulty";

            String[] data = new String[3];
            data[0] = username;
            data[1] = focusBody;
            data[2] = exDifficulty;

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getFocusBody.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d("FetchExercises", "Full Response: " + result);
                    parseExercises(result);
                } else {
                    Toast.makeText(FocusBody.this, "Error: Could not complete the request.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void parseExercises(String jsonData) {
        Log.d("ParseExercises", "JSON Data: " + jsonData);

        try {
            if (jsonData.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonData);

                if (jsonObject.has("error")) {
                    String errorMessage = jsonObject.getString("error");
                    Toast.makeText(FocusBody.this, errorMessage, Toast.LENGTH_LONG).show();
                    return;
                }

                if (jsonObject.has("exercises")) {
                    JSONArray exercisesArray = jsonObject.getJSONArray("exercises");

                    exercises.clear();
                    for (int i = 0; i < exercisesArray.length(); i++) {
                        JSONObject exerciseObject = exercisesArray.getJSONObject(i);

                        String exName = exerciseObject.getString("exname");
                        String exDesc = exerciseObject.getString("exdesc");
                        String exImg = exerciseObject.getString("eximg");
                        String activityValue = exerciseObject.getString("activity_value");

                        // Add logic to download and save the image
                        Exercise2 exercise = new Exercise2(exName, exDesc, exImg, activityValue);
                        downloadAndSaveImage(exercise); // Download and save the image

                        exercises.add(exercise);
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                } else {
                    Log.d("ParseExercises", "No exercises key in JSON.");
                }
            } else {
                Log.e("ParseExercises", "Unexpected response: " + jsonData);
                Toast.makeText(FocusBody.this, "Unexpected response format.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(FocusBody.this, "JSON parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        File file = new File(directory, exercise.getExName() + ".png"); // Change name as needed
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            exercise.setLocalImagePath(file.getAbsolutePath()); // Set the local image path
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
