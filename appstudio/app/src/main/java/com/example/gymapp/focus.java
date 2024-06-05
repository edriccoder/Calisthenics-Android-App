package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Arrays;

public class focus extends AppCompatActivity {

    Button but1, but2, but3, but4, but5, but6;
    boolean[] buttonStates = new boolean[7];
    String[] focusOptions = {"Full Body", "Arms", "Chest", "Abs", "Legs", "Back", "Cardio"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        but1 = findViewById(R.id.but1);
        but2 = findViewById(R.id.but2);
        but3 = findViewById(R.id.but3);
        but4 = findViewById(R.id.but4);
        but5 = findViewById(R.id.but5);
        but6 = findViewById(R.id.but6);

        Arrays.fill(buttonStates, false);

        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[0] = !buttonStates[0];
                if (buttonStates[0]) {
                    Arrays.fill(buttonStates, true);
                    buttonStates[0] = false;
                } else {
                    Arrays.fill(buttonStates, false);
                }
                toggleButtonState();
            }
        });

        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[1] = !buttonStates[1];
                toggleButtonState();
            }
        });

        but3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[2] = !buttonStates[2];
                toggleButtonState();
            }
        });

        but4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[3] = !buttonStates[3];
                toggleButtonState();
            }
        });

        but5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[4] = !buttonStates[4];
                toggleButtonState();
            }
        });

        but6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[5] = !buttonStates[5];
                toggleButtonState();
            }
        });

        Button button = findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (countSelectedFocus() >= 3) {
                    addSelectedFocusToDatabase();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select at least 3 focus options.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), gender.class);
                startActivity(intent);
            }
        });
    }

    private void toggleButtonState() {
        but1.setBackgroundResource(buttonStates[0] ? R.drawable.button_week_click : R.drawable.button_week);
        but2.setBackgroundResource(buttonStates[1] ? R.drawable.button_week_click : R.drawable.button_week);
        but3.setBackgroundResource(buttonStates[2] ? R.drawable.button_week_click : R.drawable.button_week);
        but4.setBackgroundResource(buttonStates[3] ? R.drawable.button_week_click : R.drawable.button_week);
        but5.setBackgroundResource(buttonStates[4] ? R.drawable.button_week_click : R.drawable.button_week);
        but6.setBackgroundResource(buttonStates[5] ? R.drawable.button_week_click : R.drawable.button_week);
    }

    private void addSelectedFocusToDatabase() {
        String username = signups.Globals.username;

        if (buttonStates[0]) {
            String focus = focusOptions[0];
            addToDatabase(username, focus);
        }

        for (int i = 1; i < buttonStates.length; i++) {
            if (buttonStates[i]) {
                String focus = focusOptions[i];
                addToDatabase(username, focus);
            }
        }
    }

    private int countSelectedFocus() {
        int count = 0;
        for (boolean state : buttonStates) {
            if (state) {
                count++;
            }
        }
        return count;
    }


    private void addToDatabase(String username, String focus) {
        String[] field = {"username", "focusbody"};
        String[] data = {username, focus};

        PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/createFocusGoal.php", "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), goals.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Failed to add focus goal. Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }
    }
}

