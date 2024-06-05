package com.example.gymapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class tracking extends Fragment {
    private int totalCalories = 0;
    private int goalCalories = 2000;
    private EditText editTextCalorie;
    private TextView textViewTotalCalories;
    private TextView textViewGoalCalories;
    private ProgressBar progressBar;
    private ListView listViewExercises;
    private TrackingAdapter exerciseAdapter;
    private List<ExerciseTracking> exerciseList = new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        editTextCalorie = view.findViewById(R.id.editTextCalorie);
        textViewTotalCalories = view.findViewById(R.id.textViewTotalCalories);
        textViewGoalCalories = view.findViewById(R.id.textViewGoalCalories);
        progressBar = view.findViewById(R.id.progressBar);
        listViewExercises = view.findViewById(R.id.listViewExercises);
        textViewGoalCalories.setText("Goal Calories: " + goalCalories);

        exerciseAdapter = new TrackingAdapter(getActivity(), exerciseList);
        listViewExercises.setAdapter(exerciseAdapter);

        String username = MainActivity.GlobalsLogin.username;

        requestQueue = Volley.newRequestQueue(getActivity());
        fetchExercises(username);


        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCalories();
            }
        });

        return view;
    }

    private void addCalories() {
        String calorieStr = editTextCalorie.getText().toString();
        if (!calorieStr.isEmpty()) {
            int calorieInt = Integer.parseInt(calorieStr);
            totalCalories += calorieInt;
            textViewTotalCalories.setText("Total Calories: " + totalCalories);
            editTextCalorie.getText().clear();
            updateProgressBar();
        }
    }

    private void updateProgressBar() {
        double progress = (double) totalCalories / goalCalories * 100;
        progressBar.setProgress((int) progress);

        if (progress > 100) {
            progressBar.setProgress(100);
        } else if (progress < 0) {
            progressBar.setProgress(0);
        }
    }

    private void fetchExercises(String username) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"username"};
                String[] data = {username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getTrackingExercise.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String exname = jsonObject.getString("exname");
                            String eximg = jsonObject.getString("eximg");

                            exerciseList.add(new ExerciseTracking(exname, eximg));
                        }
                        exerciseAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing JSON data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to complete request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
