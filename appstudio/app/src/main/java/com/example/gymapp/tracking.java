package com.example.gymapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.List;

public class tracking extends Fragment {
    private TextView workoutCountText;
    private TextView caloriesBurnedText;
    private TextView weightText; // Add a TextView for weight
    private TextView bmiText;
    private TextView heightText;
    private ProgressBar bmiProgressBar;

    // Create a tag for logging
    private static final String TAG = "TrackingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        workoutCountText = view.findViewById(R.id.workoutCountText);
        caloriesBurnedText = view.findViewById(R.id.caloriesBurnedText);
        weightText = view.findViewById(R.id.weight); // Link the TextView for weight
        bmiText = view.findViewById(R.id.bmi);
        heightText = view.findViewById(R.id.height); // New TextView for height
        bmiProgressBar = view.findViewById(R.id.bmiProgressBar);


        getExerciseCount();
        getWeight();
        getWeightAndHeight();

        return view;
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

                            int caloriesBurned = calculateCaloriesBurned(exerciseCount);
                            caloriesBurnedText.setText(String.valueOf(caloriesBurned));
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

    private void getWeight() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;

                String[] field = {"username"};
                String[] data = {username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_weight.php", "POST", field, data);

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
                            int weight = responseJson.getInt("weight");
                            weightText.setText("Current weight: " + weight + " lbs"); // Display the weight
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

    private int calculateCaloriesBurned(int exerciseCount) {
        int caloriesPerWorkout = 100;
        return exerciseCount * caloriesPerWorkout;
    }

    private void getWeightAndHeight() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;

                String[] field = {"username"};
                String[] data = {username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_BMI.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    Log.d(TAG, "Full Server Response: " + result);

                    try {
                        JSONObject responseJson = new JSONObject(result);

                        if (responseJson.has("error")) {
                            String errorMessage = responseJson.getString("error");
                            Log.e(TAG, "Error: " + errorMessage);
                            Toast.makeText(getActivity(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        } else if (responseJson.has("weight") && responseJson.has("height")) {
                            String weightStr = responseJson.getString("weight");
                            String heightStr = responseJson.getString("height");

                            int weight = Integer.parseInt(weightStr);
                            int height = Integer.parseInt(heightStr);

                            if (weight > 0 && height > 0) {
                                weightText.setText(weight + " lbs");
                                heightText.setText(height + " cm");

                                double bmi = calculateBMI(weight, height);
                                bmiText.setText(String.format("%.2f", bmi));
                                updateBMICategory(bmi); // Make sure this line is executed
                            } else {
                                Log.e(TAG, "Invalid weight or height");
                                Toast.makeText(getActivity(), "Invalid weight or height", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.e(TAG, "Weight or height data missing");
                            Toast.makeText(getActivity(), "Weight or height data missing", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing error: " + result, e);
                        Toast.makeText(getActivity(), "Error parsing server response", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Number format error: " + result, e);
                        Toast.makeText(getActivity(), "Error converting weight or height to number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to complete request";
                    Log.e(TAG, errorMsg);
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateBMICategory(double bmi) {
        bmiProgressBar.setMax(40); // Set the max to 40

        // Convert BMI to an integer and ensure it's within bounds
        int bmiProgress = (int) Math.min(bmi, 40);
        bmiProgressBar.setProgress(bmiProgress);

        // Change progress bar color based on BMI category
        if (bmi < 18.5) {
            bmiProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bmi_underweight_color)));
        } else if (bmi >= 18.5 && bmi < 24.9) {
            bmiProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bmi_normal_color)));
        } else if (bmi >= 25 && bmi < 29.9) {
            bmiProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bmi_overweight_color)));
        } else {
            bmiProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.bmi_obese_color)));
        }
    }


    private double calculateBMI(int weight, int height) {
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }
    private void updateWeightChart(List<Entry> weightEntries) {
        LineChart weightChart = getView().findViewById(R.id.lineChart);

        LineDataSet dataSet = new LineDataSet(weightEntries, "Weight");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        weightChart.setData(lineData);

        weightChart.invalidate(); // Refresh chart

        // Analyze the trend and provide feedback
        giveWeightTrendFeedback(weightEntries);
    }

    private void giveWeightTrendFeedback(List<Entry> weightEntries) {
        TextView feedbackText = getView().findViewById(R.id.weightTrendFeedback);

        if (weightEntries.size() >= 2) {
            float initialWeight = weightEntries.get(0).getY();
            float finalWeight = weightEntries.get(weightEntries.size() - 1).getY();

            if (finalWeight < initialWeight) {
                feedbackText.setText("You're losing weight. Keep it up!");
                feedbackText.setTextColor(getResources().getColor(R.color.success_color));
            } else if (finalWeight > initialWeight) {
                feedbackText.setText("You're gaining weight. Consider adjusting your routine.");
                feedbackText.setTextColor(getResources().getColor(R.color.warning_color));
            } else {
                feedbackText.setText("Your weight is stable.");
                feedbackText.setTextColor(getResources().getColor(R.color.neutral_color));
            }
        }
    }

}
