package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class weekly_goal extends AppCompatActivity {

    SeekBar seekBar;
    TextView selectedDaysText;
    String selectedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_goal);

        // Initialize SeekBar and TextView
        seekBar = findViewById(R.id.seekBar);
        selectedDaysText = findViewById(R.id.selectedDaysText);

        setupSeekBar();

        findViewById(R.id.next).setOnClickListener(v -> {
            String username = signups.Globals.username;
            if (username == null || username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username is missing. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDays != null && !selectedDays.isEmpty() && !selectedDays.equals("0")) {
                addToDatabase(username, selectedDays);
            } else {
                Toast.makeText(getApplicationContext(), "Please select the number of days.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.back).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), goals.class);
            startActivity(intent);
        });
    }

    private void setupSeekBar() {
        seekBar.setMax(7);
        seekBar.setProgress(1);

        selectedDays = "1";

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                selectedDays = String.valueOf(progress > 0 ? progress : 1);
                selectedDaysText.setText(selectedDays + " times/week");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void addToDatabase(String username, String days) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            // Ensure the fields match the expected PHP parameter names
            String[] field = {"day", "username"}; // Change 'days' to 'day'
            String[] data = {days, username};

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/addWeeklyGoal.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Weekly goal added successfully")) {
                        Intent intent = new Intent(getApplicationContext(), weight.class);
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to add weekly goal. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
