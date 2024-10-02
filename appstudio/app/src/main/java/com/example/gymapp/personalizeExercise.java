package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class personalizeExercise extends AppCompatActivity {

    private ListView listView;
    private Spinner daySpinner;
    private Spinner focusSpinner;
    private exercise_adapter adapter;
    private List<DayCount> dayCounts;
    private List<Exercise> exerciseList;
    Button done;

    private static final String TAG = "PersonalizeExercise";  // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalize_exercise);

        listView = findViewById(R.id.listView);
        daySpinner = findViewById(R.id.day);
        focusSpinner = findViewById(R.id.focus);

        exerciseList = new ArrayList<>();
        String username = signups.Globals.username;
        fetchExerciseDays(username);

        done = findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        focusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedFocus = focusSpinner.getSelectedItem().toString();
                filterExercisesByFocus(selectedFocus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update the selected day count
                String selectedDayCount = dayCounts.get(position).getCount();
                if (adapter != null) {
                    adapter.setExerciseDay(selectedDayCount); // Pass the new day to the adapter
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });



        fetchAllExercises(username);
    }

    private void fetchAllExercises(String username) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[0];
                String[] data = new String[0];

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getAllExerciseRecords.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();

                        try {
                            exerciseList = getExercises(result);
                            String selectedDayCount = dayCounts.get(daySpinner.getSelectedItemPosition()).getCount();
                            adapter = new exercise_adapter(personalizeExercise.this, exerciseList, username, selectedDayCount);
                            listView.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing exercise data", e);  // Log error
                            Toast.makeText(personalizeExercise.this, "Error parsing exercise data", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e(TAG, "Error sending request for exercises");  // Log error
                    Toast.makeText(personalizeExercise.this, "Error sending request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<Exercise> getExercises(String result) throws JSONException {
        ArrayList<Exercise> exerciseList = new ArrayList<>();

        try {
            JSONArray exerciseArray = new JSONArray(result);

            for (int i = 0; i < exerciseArray.length(); i++) {
                JSONObject exerciseObject = exerciseArray.getJSONObject(i);
                String exerciseName = exerciseObject.getString("exname");
                String description = exerciseObject.getString("exdesc");
                String imageUrl = exerciseObject.getString("eximg");
                String difficulty = exerciseObject.getString("exdifficulty");
                String focusBody = exerciseObject.getString("focusbody");
                String buildMuscle = exerciseObject.getString("BuildMuscle");
                Exercise exercise = new Exercise(exerciseName, description, imageUrl, difficulty, focusBody, buildMuscle);
                exerciseList.add(exercise);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error in JSON data structure", e);  // Log error
            throw e;
        }

        return exerciseList;
    }

    private void fetchExerciseDays(String username) {
        dayCounts = new ArrayList<>(); // Initialize the list to store day and count pairs
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[]{"username"};
                String[] data = new String[]{username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getGenerateWeek.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();

                        try {
                            ArrayList<String> days = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(result);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String day = jsonObject.getString("day");
                                String count = jsonObject.getString("count");
                                dayCounts.add(new DayCount(day, count)); // Store day and count together
                                days.add(day); // Add day for display in the spinner
                            }

                            if (!days.isEmpty()) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(personalizeExercise.this, android.R.layout.simple_spinner_item, days);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                daySpinner.setAdapter(adapter);
                            } else {
                                Toast.makeText(personalizeExercise.this, "No days found", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing day data", e);  // Log error
                            Toast.makeText(personalizeExercise.this, "Error parsing day data", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.e(TAG, "Error sending request for exercise days");  // Log error
                    Toast.makeText(personalizeExercise.this, "Error sending request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filterExercisesByFocus(String selectedFocus) {
        if (adapter == null) {
            Toast.makeText(this, "Adapter not initialized. Please wait...", Toast.LENGTH_SHORT).show();
            return;  // Exit if adapter is not yet initialized
        }

        ArrayList<Exercise> filteredList = new ArrayList<>();
        for (Exercise exercise : exerciseList) {
            if (exercise.getFocusbody().equalsIgnoreCase(selectedFocus)) {
                filteredList.add(exercise);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No exercises found for " + selectedFocus, Toast.LENGTH_SHORT).show();
        }
        adapter.filterList(filteredList);
    }
}