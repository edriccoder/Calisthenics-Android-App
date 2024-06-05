package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class exfocus_list extends AppCompatActivity {
    private ListView listView;
    private exfocus_adapter adapter;
    private List<focusExercise> exerciseList;
    private String focusbody;
    private String day;
    private String username;
    TextView focusText;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exfocus_list);

        listView = findViewById(R.id.listView);
        focusText = findViewById(R.id.focus);
        exerciseList = new ArrayList<>();
        imageButton = findViewById(R.id.imageButton);

        focusbody = getIntent().getStringExtra("focusbody");
        username = MainActivity.GlobalsLogin.username; // Get username
        day = getIntent().getStringExtra("day");

        focusText.setText(focusbody + ":" + day);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(exfocus_list.this, FocusAdd.class);
                intent.putExtra("focusbody", focusbody);
                startActivity(intent);
            }
        });

        fetchExercises(username);
    }

    private void fetchExercises(String username) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"username", "focusbody"};
                String[] data = {username, focusbody};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getFocusUser.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();

                    try {
                        JSONObject responseJson = new JSONObject(result);

                        if (responseJson.has("error")) {
                            String errorMessage = responseJson.getString("error");
                            Toast.makeText(exfocus_list.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } else if (responseJson.has("exercises")) {
                            JSONArray jsonArray = new JSONArray(responseJson.getString("exercises"));

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String exerciseName = jsonObject.getString("exname");
                                String description = jsonObject.getString("exdesc");
                                String imageUrl = jsonObject.getString("eximg");
                                String difficulty = jsonObject.getString("exdifficulty");

                                getActivity(username, exerciseName, new ActivityCallback() {
                                    @Override
                                    public void onActivityReceived(String activity) {
                                        if (activity != null) {
                                            focusExercise exercise = new focusExercise(exerciseName, description, imageUrl, difficulty, activity);
                                            exerciseList.add(exercise);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (!exerciseList.isEmpty()) {
                                                        adapter = new exfocus_adapter(exfocus_list.this, exerciseList, username);
                                                        listView.setAdapter(adapter);
                                                    } else {
                                                        Toast.makeText(exfocus_list.this, "No exercises found", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(exfocus_list.this, "Failed to fetch activity", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(exfocus_list.this, "Unknown response received", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(exfocus_list.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(exfocus_list.this, "Failed to complete request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getActivity(String username, String exname, ActivityCallback callback) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"username", "exname"};
                String[] data = {username, exname};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getActivity.php", "POST", field, data);

                if (putData.startPut() && putData.onComplete()) {
                    String result = putData.getResult();
                    callback.onActivityReceived(result);
                } else {
                    callback.onActivityReceived(null);
                }
            }
        });
    }

    private interface ActivityCallback {
        void onActivityReceived(String activity);
    }
}



