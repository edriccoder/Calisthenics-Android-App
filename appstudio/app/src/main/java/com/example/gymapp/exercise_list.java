package com.example.gymapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class exercise_list extends AppCompatActivity {
    private ListView listView;
    private exercise_adapter adapter;
    private List<Exercise> exerciseList;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        listView = findViewById(R.id.listView);
        searchView = findViewById(R.id.searchView);
        exerciseList = new ArrayList<>();

        fetchExercises(); // Move the data fetching logic to a separate method
        setupSearchView();
    }

    private void fetchExercises() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = new String[0];
            String[] data = new String[0];

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getAllExerciseRecords.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    try {
                        exerciseList = getExercises(result);

                        if (exerciseList != null && !exerciseList.isEmpty()) {
                            adapter = new exercise_adapter(exercise_list.this, exerciseList, "user12345678900", "Monday"); // Change exercise day as needed
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(exercise_list.this, "No exercises found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(exercise_list.this, "Error parsing exercise data", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(exercise_list.this, "Error sending request", Toast.LENGTH_SHORT).show();
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
                String buildMuscle = exerciseObject.optString("buildMuscle", ""); // Use optString to avoid crashes if key is absent
                Exercise exercise = new Exercise(exerciseName, description, imageUrl, difficulty, focusBody, buildMuscle);
                exerciseList.add(exercise);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }

        return exerciseList;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterExercises(newText);
                return true;
            }
        });
    }

    private void filterExercises(String query) {
        if (adapter != null) {
            ArrayList<Exercise> filteredList = new ArrayList<>();
            for (Exercise exercise : exerciseList) {
                if (exercise.getExerciseName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(exercise);
                }
            }
            adapter.filterList(filteredList);
        }
    }
}
