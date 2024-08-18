package com.example.gymapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FocusBody extends AppCompatActivity {

    private ListView listViewExercises;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise2> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_body);

        listViewExercises = findViewById(R.id.listViewExercises);
        exercises = new ArrayList<>();
        adapter = new ExerciseAdapter(this, exercises);
        listViewExercises.setAdapter(adapter);

        String focusBody = getIntent().getStringExtra("focusbody");
        String exDifficulty = getIntent().getStringExtra("exdifficulty");

        fetchExercises(focusBody, exDifficulty);
    }

    private void fetchExercises(String focusBody, String exDifficulty) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            // Starting the data upload process
            String[] field = new String[2];
            field[0] = "focusbody";
            field[1] = "exdifficulty";

            // Creating the data array
            String[] data = new String[2];
            data[0] = focusBody;
            data[1] = exDifficulty;

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getExercises.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    parseExercises(result);
                } else {
                    Toast.makeText(FocusBody.this, "Error: Could not complete the request.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void parseExercises(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray exercisesArray = jsonObject.getJSONArray("exercises");

            exercises.clear();
            for (int i = 0; i < exercisesArray.length(); i++) {
                JSONObject exerciseObject = exercisesArray.getJSONObject(i);

                String exName = exerciseObject.getString("exname");
                String exDesc = exerciseObject.getString("exdesc");
                String exImg = exerciseObject.getString("eximg");

                exercises.add(new Exercise2(exName, exDesc, exImg));
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
