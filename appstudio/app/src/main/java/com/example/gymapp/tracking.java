package com.example.gymapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class tracking extends Fragment {
    // UI Components
    private TextView workoutCountText, caloriesText, currentWeightText, exerciseTimeText;
    private ListView exerciseLogListView;
    private LineChart weightLogsChart;
    private Button settingsButton, showEMGDurationButton, showExerciseLogs, showAllLogsButton;

    // Data and Adapters
    private List<ExerciseLog> exerciseLogList;
    private ExerciseLogAdapter exerciseAdapter;

    // Networking
    private OkHttpClient client;
    private Gson gson;

    // Constants
    private static final String TAG = "TrackingFragment";
    private static final String GET_EMG_DURATION_URL = "https://calestechsync.dermocura.net/calestechsync/getEMGDuration.php"; // Replace with your actual URL

    // Define the EMGDuration model class to map JSON response
    private class EMGDuration {
        private boolean success;
        private List<String> emg_durations;
        private String error;

        public boolean isSuccess() {
            return success;
        }

        public List<String> getEmg_durations() {
            return emg_durations;
        }

        public String getError() {
            return error;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize OkHttpClient and Gson
        client = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        // Initialize UI components
        workoutCountText = view.findViewById(R.id.workoutCountText);
        caloriesText = view.findViewById(R.id.workoutCountCalories);
        currentWeightText = view.findViewById(R.id.currentWeight);
        exerciseTimeText = view.findViewById(R.id.exerciseTimeText);
        exerciseLogListView = view.findViewById(R.id.exerciseLogListView);
        weightLogsChart = view.findViewById(R.id.weightChart);
        settingsButton = view.findViewById(R.id.settingsButton);
        showEMGDurationButton = view.findViewById(R.id.showEMGDurationButton);
        showExerciseLogs = view.findViewById(R.id.showExerciseLogs);
        showAllLogsButton = view.findViewById(R.id.showAllLogsButton);

        // Initialize the exercise log list and adapter
        exerciseLogList = new ArrayList<>();
        exerciseAdapter = new ExerciseLogAdapter(getActivity(), exerciseLogList);
        exerciseLogListView.setAdapter(exerciseAdapter);

        // Set up the Settings button to edit weight
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditWeightDialog();
            }
        });

        // Set up the Show EMG Duration button
        showEMGDurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEMGDurationModal();
            }
        });

        showExerciseLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExerciseLogsDialog();
            }
        });

        showAllLogsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), exercise_logs.class);
                startActivity(intent);

            }
        });

        // Fetch and display data
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        getExerciseTime(todayDate);
        getExerciseCount();
        getCaloriesBurned(todayDate);
        getWeightLogs();
        getCurrentWeight();

        return view;
    }

    private void showExerciseLogsDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.exercise_logs_dialog, null);

        // Initialize dialog UI components
        ListView dialogListView = dialogView.findViewById(R.id.dialogExerciseLogListView);
        Button closeDialogButton = dialogView.findViewById(R.id.closeDialogButton);

        // Initialize a separate list and adapter for the dialog
        List<ExerciseLog> dialogExerciseLogList = new ArrayList<>();
        ExerciseLogAdapter dialogAdapter = new ExerciseLogAdapter(getActivity(), dialogExerciseLogList);
        dialogListView.setAdapter(dialogAdapter);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        builder.setCancelable(false); // Prevent dialog from closing on outside touch

        AlertDialog dialog = builder.create();

        // Set up the Close button
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // Fetch exercise logs and populate the dialog's ListView
        getExerciseLogs(new ExerciseLogsCallback() {
            @Override
            public void onSuccess(List<ExerciseLog> logs) {
                dialogExerciseLogList.clear();
                dialogExerciseLogList.addAll(logs);
                dialogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void showEditWeightDialog() {
        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Weight");

        // Create an input field for weight
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter your weight (kg)");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Submit", (dialog, which) -> {
            // Get the inputted weight
            String weightInput = input.getText().toString();

            if (!weightInput.isEmpty()) {
                // Call the method to send data to the server
                sendWeightToServer(weightInput);
            } else {
                Toast.makeText(getContext(), "Weight cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    private void sendWeightToServer(String weight) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;
            String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Define the form fields and data
            String[] field = {"username", "weight", "log_date"};
            String[] data = {username, weight, logDate};

            // Initialize PutData for the network request
            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insertWeightLogs.php", "POST", field, data);
            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Server response: " + result);
                Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();

                // Optionally, refresh current weight display
                getCurrentWeight();
            } else {
                Toast.makeText(getContext(), "Failed to submit weight", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentWeight() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;

            // Define the fields and data for the POST request
            String[] field = {"username"};
            String[] data = {username};

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getWeight.php", "POST", field, data);

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Server response: " + result);

                try {
                    JSONObject responseJson = new JSONObject(result);
                    String status = responseJson.getString("status");

                    if (status.equals("success")) {
                        // Retrieve and display the weight
                        double weight = responseJson.getDouble("weight");
                        currentWeightText.setText(String.format(Locale.getDefault(), "%.1f kg", weight));
                    } else {
                        // Handle error
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
        });
    }

    private void getCaloriesBurned(String date) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;

            // Define the fields and data for the POST request
            String[] field = {"username", "date"};
            String[] data = {username, date};

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
        });
    }

    private void getExerciseCount() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
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
        });
    }

    private void getExerciseLogs(ExerciseLogsCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            String[] field = {"username", "log_date"};
            String[] data = {username, todayDate};

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
                        String logDate = logEntry.getString("log_date");

                        ExerciseLog exerciseLog = new ExerciseLog(exerciseName, sets, reps, logDate);
                        logs.add(exerciseLog);
                    }

                    // Return the logs via the callback
                    callback.onSuccess(logs);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSON Parsing error: " + result, e);
                    callback.onFailure("Error parsing server response");
                }
            } else {
                String errorMsg = "Failed to complete request";
                Log.e(TAG, errorMsg);
                callback.onFailure(errorMsg);
            }
        });
    }

    /**
     * Callback interface for fetching exercise logs.
     */
    private interface ExerciseLogsCallback {
        void onSuccess(List<ExerciseLog> logs);
        void onFailure(String errorMessage);
    }

    private void getWeightLogs() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            String[] field = {"username", "log_date"};
            String[] data = {username, date};

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/executeWeightQuery.php", "POST", field, data);

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Server response: " + result);

                try {
                    JSONObject responseJson = new JSONObject(result);
                    String status = responseJson.getString("status");

                    if (status.equals("success")) {
                        JSONArray weightLogs = responseJson.getJSONArray("weight_logs");
                        ArrayList<Entry> weightEntries = new ArrayList<>();
                        ArrayList<String> logDates = new ArrayList<>();

                        // Build chart data
                        for (int i = 0; i < weightLogs.length(); i++) {
                            JSONObject logEntry = weightLogs.getJSONObject(i);
                            double weight = logEntry.getDouble("weight");
                            String logDate = logEntry.getString("log_date");

                            logDates.add(logDate);
                            weightEntries.add(new Entry(i, (float) weight));
                        }

                        // Update the chart with the weight data
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
        });
    }

    private void setWeightLogsChart(ArrayList<Entry> weightEntries, final ArrayList<String> logDates) {
        LineDataSet dataSet = new LineDataSet(weightEntries, "Weight over Time");
        dataSet.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(ColorTemplate.MATERIAL_COLORS[0]);
        dataSet.setDrawValues(false); // Hide values for cleaner look

        // Set the highlight color to red
        dataSet.setHighlightEnabled(true);
        dataSet.setHighLightColor(Color.RED); // Set highlight (crosshair) color to red

        LineData lineData = new LineData(dataSet);
        weightLogsChart.setData(lineData);

        // Customize the chart appearance
        weightLogsChart.getDescription().setEnabled(false);

        // Set up X-axis to show log dates
        XAxis xAxis = weightLogsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        // Rotate the X-axis labels for better visibility
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setGranularity(1f);

        // Set custom value formatter for X-axis to show dates
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < logDates.size()) {
                    return logDates.get(index);
                } else {
                    return "";
                }
            }
        });

        YAxis leftAxis = weightLogsChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = weightLogsChart.getAxisRight();
        rightAxis.setEnabled(false);

        weightLogsChart.setExtraBottomOffset(40f);

        Legend legend = weightLogsChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        // Add a listener to highlight when tapped
        weightLogsChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}

            @Override
            public void onChartLongPressed(MotionEvent me) {}

            @Override
            public void onChartDoubleTapped(MotionEvent me) {}

            @Override
            public void onChartSingleTapped(MotionEvent me) {
                // Enable highlight on click
                Highlight highlight = weightLogsChart.getHighlightByTouchPoint(me.getX(), me.getY());
                if (highlight != null) {
                    weightLogsChart.highlightValue(highlight); // Highlight with red color
                }
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {}
        });

        // Refresh the chart
        weightLogsChart.invalidate();
    }



    private void getExerciseTime(String logDate) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;

            String[] field = {"username", "log_date"};
            String[] data = {username, logDate};

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getExerciseTime.php", "POST", field, data);

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Server response: " + result);

                try {
                    JSONObject responseJson = new JSONObject(result);
                    String status = responseJson.getString("status");

                    if (status.equals("success")) {
                        int exerciseTime = responseJson.getInt("exerciseTime");
                        // Convert seconds to a more readable format, e.g., HH:mm:ss
                        String formattedTime = formatSecondsToTime(exerciseTime);
                        // Display the exercise time
                        exerciseTimeText.setText(formattedTime);
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
        });
    }

    private String formatSecondsToTime(int seconds) {
        int hrs = seconds / 3600;
        int mins = (seconds % 3600) / 60;
        int secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs);
    }

    private void showEMGDurationModal() {
        // Inflate the modal layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View modalView = inflater.inflate(R.layout.emg_duration_dialog, null);

        // Initialize modal views
        TextView belowEasyText = modalView.findViewById(R.id.belowEasyTextModal);
        TextView easyText = modalView.findViewById(R.id.easyTextModal);
        TextView mediumText = modalView.findViewById(R.id.mediumTextModal);
        TextView hardText = modalView.findViewById(R.id.hardTextModal);
        Button closeModalButton = modalView.findViewById(R.id.closeModalButton);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(modalView);
        AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();

        // Fetch EMG Duration Data
        fetchEMGDurationData(belowEasyText, easyText, mediumText, hardText, dialog);

        // Set close button functionality
        closeModalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void fetchEMGDurationData(TextView belowEasy, TextView easy, TextView medium, TextView hard, AlertDialog dialog) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String username = MainActivity.GlobalsLogin.username;
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Define the fields and data for the POST request
            String[] field = {"username", "date"};
            String[] data = {username, date};

            // Initialize PutData for the network request
            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getEMGDuration.php", "POST", field, data);

            if (putData.startPut() && putData.onComplete()) {
                String result = putData.getResult();
                Log.d(TAG, "Server response: " + result);

                try {
                    // Parse the JSON response
                    JSONObject responseJson = new JSONObject(result);

                    // Check for "success" in the response
                    if (responseJson.getBoolean("success")) {
                        // Retrieve EMG duration data as an object
                        JSONObject emgDurations = responseJson.getJSONObject("emg_durations");

                        // Set the values for belowEasy, easy, medium, and hard TextViews
                        belowEasy.setText("Minimal Effort: " + emgDurations.getDouble("below_easy_seconds") + " seconds");
                        easy.setText("Easy: " + emgDurations.getDouble("easy_seconds") + " seconds");
                        medium.setText("Medium: " + emgDurations.getDouble("medium_seconds") + " seconds");
                        hard.setText("Hard: " + emgDurations.getDouble("hard_seconds") + " seconds");
                    } else {
                        // Handle error in case of no success
                        String message = responseJson.getString("error");
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
        });
    }

}
