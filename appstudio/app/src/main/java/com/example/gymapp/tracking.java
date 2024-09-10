package com.example.gymapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log; // Import Log class

import org.json.JSONException;
import org.json.JSONObject;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.text.SimpleDateFormat;
import java.util.Date;

public class tracking extends Fragment {
    private TextView workoutCountText;
    private TextView caloriesBurnedText; // Optional: for future use

    // Create a tag for logging
    private static final String TAG = "TrackingFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        workoutCountText = view.findViewById(R.id.workoutCountText);
        caloriesBurnedText = view.findViewById(R.id.caloriesBurnedText); // Optional

        // Call the function to get the exercise count for today
        getExerciseCount();

        return view;
    }

    private void getExerciseCount() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String username = MainActivity.GlobalsLogin.username;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date());

                String[] field = {"username", "date"};
                String[] data = {username, date};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/get_exercise_count.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    try {
                        // Check if the result is valid JSON
                        if (result.startsWith("{") || result.startsWith("[")) {
                            JSONObject responseJson = new JSONObject(result);

                            // Check for errors in the response
                            if (responseJson.has("error")) {
                                String errorMessage = responseJson.getString("error");
                                Log.e(TAG, errorMessage); // Log the error message
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                // Assuming the response contains the exercise count
                                int exerciseCount = responseJson.getInt("exercise_count");
                                workoutCountText.setText(String.valueOf(exerciseCount));
                                Log.d(TAG, "Exercise count: " + exerciseCount); // Log the exercise count
                            }
                        } else {
                            // If the response is not JSON
                            String errorMsg = "Received non-JSON response: " + result;
                            Log.e(TAG, errorMsg);
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        String errorMsg = "Error retrieving data";
                        Log.e(TAG, errorMsg, e); // Log the error with exception
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to complete request";
                    Log.e(TAG, errorMsg); // Log the failure message
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
