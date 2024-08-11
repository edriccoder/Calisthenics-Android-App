package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Arrays;

public class goals extends AppCompatActivity {
    Button but1, but2, but3;
    Button[] buttons;
    String[] goalOptions = {"Loss Weight", "Build Muscle", "Keep Fit"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        but1 = findViewById(R.id.but1);
        but2 = findViewById(R.id.but2);
        but3 = findViewById(R.id.but3);

        buttons = new Button[]{but1, but2, but3};

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectButton(index);
                }
            });
        }

        Button button = findViewById(R.id.next);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addSelectedFocusToDatabase();
            }
        });

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), focus.class);
                startActivity(intent);
            }
        });
    }

    private void selectButton(int index) {
        for (int i = 0; i < buttons.length; i++) {
            if (i == index) {
                buttons[i].setBackgroundResource(R.drawable.button_week_click);
            } else {
                buttons[i].setBackgroundResource(R.drawable.button_week);
            }
        }
    }

    private void addSelectedFocusToDatabase() {
        String username = signups.Globals.username;
        String selectedActivity = "";

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.button_week_click).getConstantState())) {
                selectedActivity = goalOptions[i];
                break;
            }
        }

        if (!selectedActivity.isEmpty()) {
            addToDatabase(username, selectedActivity);
        } else {
            Toast.makeText(getApplicationContext(), "Please select a goal", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToDatabase(String username, String activity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"username", "activity"};
                String[] data = {username, activity};

                    PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/createActivityGoal.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), level.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add focus goal. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
