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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class tracking extends Fragment {
    private TextView workoutCountText, caloriesText, weightLogsText;  // TextView to display weight logs
    private ListView exerciseLogListView;
    private List<ExerciseLog> exerciseLogList;
    private ExerciseLogAdapter exerciseAdapter;
    private ExerciseLogAdapter adapter;
    private LineChart weightLogsChart;
    private static final String TAG = "TrackingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        workoutCountText = view.findViewById(R.id.workoutCountText);
        exerciseLogListView = view.findViewById(R.id.exerciseLogListView);
        exerciseLogList = new ArrayList<>();

        // Set up adapter for exercise logs
        exerciseAdapter = new ExerciseLogAdapter(getActivity(), exerciseLogList);
        exerciseLogListView.setAdapter(exerciseAdapter);
        adapter = new ExerciseLogAdapter(getActivity(), exerciseLogList);
        exerciseLogListView.setAdapter(adapter);

        caloriesText = view.findViewById(R.id.workoutCountCalories);
        weightLogsChart = view.findViewById(R.id.weightChart);

        // Fetch data
        getExerciseCount();
        getExerciseLogs();
        getCaloriesBurned();
        getWeightLogs();  // Call getWeightLogs to fetch and display weight logs

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

    private void getWeightLogs() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;

                // Get the current date
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Define fields and data to send in the POST request
                String[] field = {"username", "log_date"};
                String[] data = {username, date};

                // Use PutData to send a POST request
                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/executeWeightQuery.php", "POST", field, data);

                // Check if the request is successful
                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d(TAG, "Server response: " + result);

                    try {
                        JSONObject responseJson = new JSONObject(result);
                        String status = responseJson.getString("status");

                        if (status.equals("success")) {
                            JSONArray weightLogs = responseJson.getJSONArray("weight_logs");
                            ArrayList<Entry> weightEntries = new ArrayList<>();  // To hold the weight data
                            ArrayList<String> logDates = new ArrayList<>();      // To hold the log dates

                            // Iterate over the JSON array and build a list of weight entries for the chart
                            for (int i = 0; i < weightLogs.length(); i++) {
                                JSONObject logEntry = weightLogs.getJSONObject(i);
                                double weight = logEntry.getDouble("weight");
                                String logDate = logEntry.getString("log_date");

                                // Add logDate to the logDates list
                                logDates.add(logDate);

                                // Use the index (i) as the x-axis value and weight as the y-axis value
                                weightEntries.add(new Entry(i, (float) weight));
                            }

                            // Set the data in the LineChart and provide logDates for X-axis
                            setWeightLogsChart(weightEntries, logDates);
                        } else {
                            String message = responseJson.getString("message");
                            Log.e(TAG, message);
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

    // Method to configure and display the weight logs on the chart
    private void setWeightLogsChart(ArrayList<Entry> weightEntries, final ArrayList<String> logDates) {
        LineDataSet dataSet = new LineDataSet(weightEntries, "Weight over Time");
        dataSet.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        weightLogsChart.setData(lineData);

        // Customize the chart appearance
        weightLogsChart.getDescription().setEnabled(false);

        // Set up X-axis to show log dates
        XAxis xAxis = weightLogsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Rotate the X-axis labels for better visibility
        xAxis.setLabelRotationAngle(-45f);  // Rotates the labels 45 degrees
        xAxis.setGranularity(1f);           // Ensure it labels each data point

        // Set custom value formatter for X-axis to show dates
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Return the corresponding log date for the given index (value)
                int index = (int) value;
                if (index >= 0 && index < logDates.size()) {
                    return logDates.get(index);
                } else {
                    return ""; // If out of bounds, return empty string
                }
            }
        });

        YAxis leftAxis = weightLogsChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = weightLogsChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Increase bottom padding to avoid overlapping of the X-axis labels and legend
        weightLogsChart.setExtraBottomOffset(40f); // Adds extra space below the chart

        // Customize the legend and position it below the X-axis
        weightLogsChart.getLegend().setEnabled(true); // Enable the legend
        weightLogsChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // Set legend to the bottom
        weightLogsChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // Center the legend
        weightLogsChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL); // Make the legend horizontal
        weightLogsChart.getLegend().setDrawInside(false); // Make sure the legend is drawn outside the chart bounds

        // Refresh the chart
        weightLogsChart.invalidate();
    }
}

