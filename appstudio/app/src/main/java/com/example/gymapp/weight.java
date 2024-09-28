package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

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
                            Intent intent = new Intent(getApplicationContext(), generate_week.class);
                            startActivity(intent);
                            generateExercisePlan(username);
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
                // Go back to the previous screen if the back button is pressed
                finish();
            }
        });
    }

    private void generateExercisePlan(String username) {

        String[] exerciseField = {"username"};
        String[] exerciseData = {username};

        PutData putExerciseData = new PutData("https://calestechsync.dermocura.net/calestechsync/generateAndInsertExercise.php", "POST", exerciseField, exerciseData);
        putExerciseData.startPut();
        if (putExerciseData.onComplete()) {
            String exerciseResult = putExerciseData.getResult();
            Toast.makeText(weight.this, exerciseResult, Toast.LENGTH_SHORT).show();

            if (exerciseResult.equals("Weekly exercise plan generated and saved successfully.")) {
                // Stay on the same activity and just show the success message
                Toast.makeText(weight.this, "Exercise plan created successfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(weight.this, "Failed to generate exercise plan.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
