package com.example.gymapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class exercise_logs extends AppCompatActivity {

    private Spinner logDateSpinner;
    private ListView exerciseLogListView;
    private ExerciseLogAdapter2 exerciseLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_logs);

        logDateSpinner = findViewById(R.id.logDateSpinner);
        exerciseLogListView = findViewById(R.id.dialogExerciseLogListView);
        exerciseLogAdapter = new ExerciseLogAdapter2(this, new ArrayList<>());
        exerciseLogListView.setAdapter(exerciseLogAdapter);

        fetchLogDates();

        logDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = (String) parent.getItemAtPosition(position);
                getExerciseLogs(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void fetchLogDates() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;

            // Check if username is valid
            if (username == null || username.isEmpty()) {
                Toast.makeText(this, "Username is invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            // Define the fields and data for the POST request
            String[] field = {"username"};
            String[] data = {username};

            // URL for fetching log dates
            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_logDate.php", "POST", field, data);

            // Start the request and process the response
            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Server response: " + result);

                if (result.startsWith("[")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        List<String> logDates = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            logDates.add(jsonArray.getString(i));
                        }

                        runOnUiThread(() -> populateSpinner(logDates));

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: " + result, e);
                        runOnUiThread(() -> Toast.makeText(this, "Error parsing server response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Log.e(TAG, "Unexpected server response: " + result);
                    runOnUiThread(() -> Toast.makeText(this, "Server returned an error: " + result, Toast.LENGTH_SHORT).show());
                }
            } else {
                String errorMsg = "Failed to complete request";
                Log.e(TAG, errorMsg);
                runOnUiThread(() -> Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show());
            }

        });
    }


    private void populateSpinner(List<String> logDates) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, logDates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logDateSpinner.setAdapter(adapter);
    }

    private void getExerciseLogs(String selectedDate) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;

            String[] field = {"username", "log_date"};
            String[] data = {username, selectedDate};

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_exercise_logs.php", "POST", field, data);

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Server response: " + result);

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    List<ExerciseLog> logs = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject logEntry = jsonArray.getJSONObject(i);
                        String exerciseName = logEntry.getString("exercise_name");
                        int sets = logEntry.getInt("sets");
                        int reps = logEntry.getInt("reps");

                        ExerciseLog exerciseLog = new ExerciseLog(exerciseName, sets, reps, selectedDate);
                        logs.add(exerciseLog);
                    }

                    runOnUiThread(() -> {
                        // Clear the existing data in the adapter and add new logs
                        exerciseLogAdapter.clear();
                        exerciseLogAdapter.addAll(logs);
                        exerciseLogAdapter.notifyDataSetChanged();
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "JSON Parsing error: " + result, e);
                    runOnUiThread(() -> Toast.makeText(this, "Error parsing exercise logs", Toast.LENGTH_SHORT).show());
                }
            } else {
                Log.e(TAG, "Failed to complete request");
                runOnUiThread(() -> Toast.makeText(this, "Failed to retrieve exercise logs", Toast.LENGTH_SHORT).show());
            }
        });
    }


}
