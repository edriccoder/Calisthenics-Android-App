package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gymapp.Exercise;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class weight extends AppCompatActivity {
    EditText weight, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        weight = findViewById(R.id.weight);
        height = findViewById(R.id.height);

        Button button = findViewById(R.id.done);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String weightTxt, heightTxt;
                String username = signups.Globals.username;
                weightTxt = weight.getText().toString();
                heightTxt = height.getText().toString();

                if (!weightTxt.isEmpty() && !heightTxt.isEmpty()) {

                    String[] bmiField = {"username", "weight", "height"};
                    String[] bmiData = {username, weightTxt, heightTxt};

                    PutData putBmiData = new PutData("https://calestechsync.dermocura.net/calestechsync/createBmi.php", "POST", bmiField, bmiData);
                    putBmiData.startPut();
                    if (putBmiData.onComplete()) {
                        String bmiResult = putBmiData.getResult();
                        Toast.makeText(weight.this, bmiResult, Toast.LENGTH_SHORT).show();
                        if (bmiResult.equals("BMI Created Successfully")) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            fetchExercisesFromServer(username);
                        }
                    }
                } else {
                    Toast.makeText(weight.this, "All fields required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), weekly_goal.class);
                startActivity(intent);
            }
        });
    }

    private void fetchExercisesFromServer(String username) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[1];
                field[0] = "username";
                String[] data = new String[1];
                data[0] = username;

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/generateAndInsertExercise.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Log.d("ExerciseData", "Response: " + result);

                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.has("error")) {
                                String error = jsonObject.getString("error");
                                Toast.makeText(weight.this, error, Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.has("exercises")) { // Handle exercises data
                                JSONArray jsonArray = jsonObject.getJSONArray("exercises");
                                if (jsonArray.length() > 0) {
                                    ArrayList<String> exerciseList = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject exercise = jsonArray.getJSONObject(i);
                                        String exerciseName = exercise.getString("exname");
                                        String exerciseDesc = exercise.getString("exdesc");
                                        String exerciseImg = exercise.getString("eximg");
                                        String exerciseDifficulty = exercise.getString("exdifficulty");
                                        String focusBody = exercise.getString("focusbody");

                                        exerciseList.add(exerciseName + " - " + exerciseDesc + " - " + exerciseImg + " - " + exerciseDifficulty + " - " + focusBody);
                                    }
                                    insertIntoExRecordUser(username, exerciseList);
                                } else {
                                    Toast.makeText(weight.this, "No exercises found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(weight.this, "Unknown response from server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(weight.this, "Error parsing exercise data", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(weight.this, "Error sending request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void insertIntoExRecordUser(String username, ArrayList<String> exercises) {
        for (String exercise : exercises) {
            String[] exerciseData = exercise.split(" - ");
            String exname = exerciseData[0];
            String exdesc = exerciseData[1];
            String eximg = exerciseData[2];
            String exdifficulty = exerciseData[3];
            String focusbody = exerciseData[4];

            PutData putData = getPutData(username, exname, exdesc, eximg, exdifficulty, focusbody);
            putData.startPut();

            if (putData.onComplete()) {
                String result = putData.getResult();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("success")) {
                        String successMessage = jsonObject.getString("success");
                        Toast.makeText(weight.this, successMessage, Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.has("error")) {
                        String errorMessage = jsonObject.getString("error");
                        Toast.makeText(weight.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(weight.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(weight.this, "Error sending request", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @NonNull
    private PutData getPutData(String username, String exname, String exdesc, String eximg, String exdifficulty, String focusbody) {
        PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/insertIntoExRecordUser.php", "POST",
                new String[]{"username", "exname", "exdesc", "eximg", "exdifficulty", "focusbody"},
                new String[]{username, exname, exdesc, eximg, exdifficulty, focusbody});
        return putData;
    }
}
