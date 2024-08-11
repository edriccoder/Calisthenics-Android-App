package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymapp.weight;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class weekly_goal extends AppCompatActivity {

    Spinner spinner;
    String selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_goal);

        spinner = findViewById(R.id.spinner2);

        setupSpinner();

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedDay != null && !selectedDay.isEmpty()) {
                    addToDatabase(selectedDay);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select a day.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), goals.class);
                startActivity(intent);
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.week_days_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDay = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDay = null;
            }
        });
    }

    private void addToDatabase(String day) {
        String username = signups.Globals.username;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
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