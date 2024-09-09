package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FocusBody extends AppCompatActivity {

    private ExerciseAdapter adapter;
    private ArrayList<Exercise2> exercises;

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

                        exercises.add(new Exercise2(exName, exDesc, exImg, activityValue));
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
}
