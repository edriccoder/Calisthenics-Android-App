package com.example.gymapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.Calendar;

public class age_assessment extends AppCompatActivity {

    private TextView birthdayInput;
    private int selectedYear, selectedMonth, selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_assessment);

        birthdayInput = findViewById(R.id.birthdayInput);

        birthdayInput.setOnClickListener(v -> showDatePickerDialog());

        // Assuming you have a button to submit the data
        findViewById(R.id.next).setOnClickListener(v -> {
            if (selectedYear != 0 && selectedMonth != 0 && selectedDay != 0) {
                String username = signups.Globals.username;
                submitDataToServer(username); // replace with actual username
            } else {
                Toast.makeText(getApplicationContext(), "Please select your birthday first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(age_assessment.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedYear = year1;
                    selectedMonth = monthOfYear;
                    selectedDay = dayOfMonth;
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    birthdayInput.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void submitDataToServer(String username) {
        // Calculate the age from the birthday
        final Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - selectedYear;

        // Adjust age if birthday hasn't occurred this year yet
        if (today.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        // Prepare the birthday in the format (YYYY-MM-DD) for the database
        String birthday = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;

        // Insert the birthday and age into the database
        addToDatabase(username, birthday, String.valueOf(age));
    }

    private void addToDatabase(String username, String birthday, String age) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String[] field = {"username", "birthday", "age"};
            String[] data = {username, birthday, age};

            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/createAge.php", "POST", field, data);
            if (putData.startPut()) {
                if (putData.onComplete()) {
                    String result = putData.getResult();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    if (result.equals("Age and Birthday Created/Updated Successfully")) {
                        Intent intent = new Intent(getApplicationContext(), focus.class);
                        startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to add age and birthday. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
