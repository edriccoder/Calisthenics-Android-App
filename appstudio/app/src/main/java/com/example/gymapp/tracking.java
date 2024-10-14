package com.example.gymapp;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.ArrayList;
import java.util.List;

public class tracking extends Fragment {
    private TextView workoutCountText, caloriesText;
    private ListView exerciseLogListView;
    private List<ExerciseLog> exerciseLogList;
    private ExerciseLogAdapter adapter;
    private static final String TAG = "TrackingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        workoutCountText = view.findViewById(R.id.workoutCountText);
        exerciseLogListView = view.findViewById(R.id.exerciseLogListView);
        exerciseLogList = new ArrayList<>();
        adapter = new ExerciseLogAdapter(getActivity(), exerciseLogList);
        exerciseLogListView.setAdapter(adapter);
        caloriesText = view.findViewById(R.id.workoutCountCalories);

        getExerciseCount();
        getExerciseLogs();
        getCaloriesBurned();

        return view;
    }

    private void getCaloriesBurned() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;

                String[] field = {"username"};
                String[] data = {username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getTotalCaloriesBurned.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d(TAG, "Server response: " + result);

                    try {
                        JSONObject responseJson = new JSONObject(result);

                        // Check for any error in the response
                        if (responseJson.has("success") && responseJson.getBoolean("success")) {
                            // Get the total calories burned
                            float totalCalories = (float) responseJson.getDouble("total_calories");
                            caloriesText.setText(String.valueOf(totalCalories));
                        } else {
                            String errorMessage = responseJson.getString("message");
                            Log.e(TAG, errorMessage);
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Parsing error: " + result, e);
                        Toast.makeText(getActivity(), "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to complete request";
                    Log.e(TAG, errorMsg);
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getExerciseCount() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;

                String[] field = {"username"};
                String[] data = {username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_exercise_count.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d(TAG, "Server response: " + result);

                    try {
                        JSONObject responseJson = new JSONObject(result);

                        if (responseJson.has("error")) {
                            String errorMessage = responseJson.getString("error");
                            Log.e(TAG, errorMessage);
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            int exerciseCount = responseJson.getInt("exercise_count");
                            workoutCountText.setText(String.valueOf(exerciseCount));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Parsing error: " + result, e);
                        Toast.makeText(getActivity(), "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to complete request";
                    Log.e(TAG, errorMsg);
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getExerciseLogs() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;

                String[] field = {"username"};
                String[] data = {username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_exercise_logs.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d(TAG, "Server response: " + result);

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        exerciseLogList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject logEntry = jsonArray.getJSONObject(i);
                            String exerciseName = logEntry.getString("exercise_name");
                            int sets = logEntry.getInt("sets");
                            int reps = logEntry.getInt("reps");
                            String logDate = logEntry.getString("log_date");

                            ExerciseLog exerciseLog = new ExerciseLog(exerciseName, sets, reps, logDate);
                            exerciseLogList.add(exerciseLog);
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "JSON Parsing error: " + result, e);
                        Toast.makeText(getActivity(), "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to complete request";
                    Log.e(TAG, errorMsg);
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
