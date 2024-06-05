package com.example.gymapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FocusAdd extends AppCompatActivity {

    private ListView listView;
    private FocusAdapter adapter;
    private String focusbody;
    private List<ExerciseFocus> exerciseList = new ArrayList<>();
    private RequestQueue requestQueue;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_add);

        focusbody = getIntent().getStringExtra("focusbody");
        textView = findViewById(R.id.text_view);
        textView.setText(focusbody);

        listView = findViewById(R.id.list_view);
        adapter = new FocusAdapter(this, exerciseList, focusbody);
        listView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(this);

        fetchExercises(focusbody);
    }

    private void fetchExercises(String focusbody) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"focusbody"}; // Define the field for the POST request
                String[] data = {focusbody}; // Pass the focusbody data

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getFocus.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            parseJsonResponse(jsonResponse); // Call your existing parsing method
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FocusAdd.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(FocusAdd.this, "Error sending request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void parseJsonResponse(JSONObject jsonResponse) {
        try {
            if (jsonResponse.has("exercises")) {
                JSONArray exercisesArray = jsonResponse.getJSONArray("exercises");

                if (exercisesArray.length() > 0) {
                    for (int i = 0; i < exercisesArray.length(); i++) {
                        JSONObject exerciseObject = exercisesArray.getJSONObject(i);
                        String name = exerciseObject.getString("exname");
                        String description = exerciseObject.getString("exdesc");
                        String imageUrl = exerciseObject.getString("eximg");
                        String difficulty = exerciseObject.getString("exdifficulty");

                        ExerciseFocus exercise = new ExerciseFocus(name, description, imageUrl, difficulty);
                        exerciseList.add(exercise);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "No exercises found", Toast.LENGTH_SHORT).show();
                }
            } else if (jsonResponse.has("error")) {
                String errorMessage = jsonResponse.getString("error");
                Toast.makeText(this, "Error fetching exercises: " + errorMessage, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unknown response received", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
        }
    }
}
