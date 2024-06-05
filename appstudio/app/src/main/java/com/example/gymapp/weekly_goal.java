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

import com.example.gymapp.weight;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Arrays;

public class weekly_goal extends AppCompatActivity {

    Button bt1, bt2, bt3, bt4, bt5, bt6, bt7;
    boolean[] buttonStates = new boolean[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_goal);

        bt1 = findViewById(R.id.button1);
        bt2 = findViewById(R.id.button2);
        bt3 = findViewById(R.id.button3);
        bt4 = findViewById(R.id.button4);
        bt5 = findViewById(R.id.button5);
        bt6 = findViewById(R.id.button6);
        bt7 = findViewById(R.id.button7);

        Arrays.fill(buttonStates, false);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[0] = !buttonStates[0];
                toggleButtonState(bt1, buttonStates[0]);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[1] = !buttonStates[1];
                toggleButtonState(bt2, buttonStates[1]);
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[2] = !buttonStates[2];
                toggleButtonState(bt3, buttonStates[2]);
            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[3] = !buttonStates[3];
                toggleButtonState(bt4, buttonStates[3]);
            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[4] = !buttonStates[4];
                toggleButtonState(bt5, buttonStates[4]);
            }
        });

        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[5] = !buttonStates[5];
                toggleButtonState(bt6, buttonStates[5]);
            }
        });

        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStates[6] = !buttonStates[6];
                toggleButtonState(bt7, buttonStates[6]);
            }
        });

        Button button = findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (countSelectedButtons() >= 3) {
                    addSelectedButtonsToDatabase();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select at least 3 days.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), goals.class);
                startActivity(intent);
            }
        });
    }

    private void toggleButtonState(Button button, boolean state) {
        if (state) {
            button.setBackground(getResources().getDrawable(R.drawable.button_week_click));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.button_week));
        }
    }

    private int countSelectedButtons() {
        int count = 0;
        for (boolean state : buttonStates) {
            if (state) {
                count++;
            }
        }
        return count;
    }

    private void addSelectedButtonsToDatabase() {
        for (int i = 0; i < buttonStates.length; i++) {
            if (buttonStates[i]) {
                String day = getDayFromIndex(i);
                addToDatabase(day);
            }
        }
    }

    private String getDayFromIndex(int index) {
        switch (index) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
            default:
                return "";
        }
    }

    private void addToDatabase(String day) {
        String username = signups.Globals.username;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[2];
                field[0] = "day";
                field[1] = "username";

                String[] data = new String[2];
                data[0] = day;
                data[1] = username;

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/addWeeklyGoal.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (result.equals("Weekly goal added successfully")) {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), weight.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add weekly goal. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
