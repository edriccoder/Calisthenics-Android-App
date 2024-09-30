package com.example.gymapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class weekly_generate extends AppCompatActivity {

    private ListView listViewExercises;
    private ExerciseAdapter adapter;
    private ArrayList<Exercise2> exerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_generate);

        listViewExercises = findViewById(R.id.listViewExercises);
        exerciseList = new ArrayList<>();

        String username = MainActivity.GlobalsLogin.username;
        Log.e("Username", "Username: " + username);
        int count = getIntent().getIntExtra("count", -1);
        Log.e("WeeklyGenerateActivity", "Count: " + count);

        if (count != -1) {
            fetchExercises(username, String.valueOf(count)); // Ensure count is passed as String
        } else {
            Toast.makeText(this, "Invalid exercise day", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchExercises(String username, String exerciseDay) {
        String url = "https://calestechsync.dermocura.net/calestechsync/get_weeklyGoal.php";

        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("username", username);
            requestParams.put("exercise_day", exerciseDay);
            Log.d("Request Params", requestParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Volley Response", response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray exercises = response.optJSONArray("data");

                                if (exercises != null && exercises.length() > 0) {
                                    for (int i = 0; i < exercises.length(); i++) {
                                        JSONObject exerciseObj = exercises.getJSONObject(i);

                                        Exercise2 exercise = new Exercise2(
                                                exerciseObj.getString("exercise_name"),
                                                exerciseObj.getString("exdesc"),
                                                exerciseObj.getString("eximg"),
                                                exerciseObj.getString("activity_goal")
                                        );
                                        exerciseList.add(exercise);
                                    }

                                    adapter = new ExerciseAdapter(weekly_generate.this, exerciseList);
                                    listViewExercises.setAdapter(adapter);
                                } else {
                                    Toast.makeText(weekly_generate.this, "No exercises found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(weekly_generate.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(weekly_generate.this, "Data parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        if (error.networkResponse != null) {
                            String rawResponse = new String(error.networkResponse.data);
                            Log.e("Raw Response", rawResponse);
                        }
                        Toast.makeText(weekly_generate.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }
}
