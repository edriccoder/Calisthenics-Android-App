package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class level extends AppCompatActivity {

    Button but1, but2, but3;
    Button[] buttons;
    String[] levelOptions = {"Beginner", "Intermediate", "Advance"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level);

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
        String selectedLevel = "";

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.button_week_click).getConstantState())) {
                selectedLevel = levelOptions[i];
                break;
            }
        }

        if (!selectedLevel.isEmpty()) {
            addToDatabase(username, selectedLevel);
        } else {
            Toast.makeText(getApplicationContext(), "Please select a level", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToDatabase(String username, String levels) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"username", "levels"};
                String[] data = {username, levels};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/createLevel.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), weekly_goal.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to add level. Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
