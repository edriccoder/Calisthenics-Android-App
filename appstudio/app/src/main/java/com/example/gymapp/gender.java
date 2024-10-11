package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Arrays;

public class gender extends AppCompatActivity {
    ImageButton male, female;
    boolean[] buttonStates = new boolean[2];
    String[] genderOptions = {"Male", "Female"};
    LinearLayout malebox, femalebox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        malebox = findViewById(R.id.malebox);
        femalebox = findViewById(R.id.femalebox);

        Arrays.fill(buttonStates, false);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[0] = true;
                buttonStates[1] = false;
                toggleButtonState();
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[0] = false;
                buttonStates[1] = true;
                toggleButtonState();
            }
        });

        Button button = findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonStates[0] || buttonStates[1]) {
                    addSelectedGenderToDatabase();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select your gender", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), age_assessment.class);
                startActivity(intent);
            }
        });
    }

    private void toggleButtonState() {
        if (buttonStates[0]) {
            malebox.setBackground(getResources().getDrawable(R.drawable.button_week_click));
            femalebox.setBackground(getResources().getDrawable(R.drawable.button_week));
        } else if (buttonStates[1]) {
            malebox.setBackground(getResources().getDrawable(R.drawable.button_week));
            femalebox.setBackground(getResources().getDrawable(R.drawable.button_week_click));
        }
    }

    private void addSelectedGenderToDatabase() {
        String username = signups.Globals.username;
        String selectedGender = "";

        if (buttonStates[0]) {
            selectedGender = genderOptions[0];
        } else if (buttonStates[1]) {
            selectedGender = genderOptions[1];
        }

        addToDatabase(username, selectedGender);
    }

    private void addToDatabase(String username, String gender) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"username", "gender"};
                String[] data = {username, gender};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/createGender.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), focus.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add focus goal. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
