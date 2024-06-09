package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class body_assessment extends AppCompatActivity {
    private EditText editTextUserId, editTextHeight, editTextWeight, editTextFocusBody, editTextGoal, editTextLevel, editTextWeeklyGoal;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_body_assessment);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextFocusBody = findViewById(R.id.editTextFocusBody);
        editTextGoal = findViewById(R.id.editTextGoal);
        editTextLevel = findViewById(R.id.editTextLevel);
        editTextWeeklyGoal = findViewById(R.id.editTextWeeklyGoal);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        String username = MainActivity.GlobalsLogin.username;

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = editTextUserId.getText().toString();
                String height = editTextHeight.getText().toString();
                String weight = editTextWeight.getText().toString();
                String focusBody = editTextFocusBody.getText().toString();
                String goal = editTextGoal.getText().toString();
                String level = editTextLevel.getText().toString();
                String weeklyGoal = editTextWeeklyGoal.getText().toString();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String[] field = new String[7];
                        field[0] = "username";
                        field[1] = "height";
                        field[2] = "weight";
                        field[3] = "focus_body";
                        field[4] = "goal";
                        field[5] = "level";
                        field[6] = "weekly_goal";

                        String[] data = new String[7];
                        data[0] = username  ;
                        data[1] = height;
                        data[2] = weight;
                        data[3] = focusBody;
                        data[4] = goal;
                        data[5] = level;
                        data[6] = weeklyGoal;

                        PutData putData = new PutData("https://yourserver.com/insertBodyAssessment.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                if(result.equals("Body assessment data inserted successfully!")){
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                    // Handle success (if needed, navigate to another activity)
                                } else {
                                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}