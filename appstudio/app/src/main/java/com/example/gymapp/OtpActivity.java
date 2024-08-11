package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OtpActivity extends AppCompatActivity {
    Button resendOtpButton, verifyOtpButton;
    EditText otpField;
    TextView timerTextView, emailTextView, error;

    String email, name, password, username;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpField = findViewById(R.id.otpField);
        resendOtpButton = findViewById(R.id.resendOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        timerTextView = findViewById(R.id.timerTextView);
        emailTextView = findViewById(R.id.emailTextView);
        error = findViewById(R.id.error);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        username = getIntent().getStringExtra("username");

        emailTextView.setText("Enter the code that we sent to: " + email);

        resendOtpButton.setEnabled(false);

        resendOtpButton.setOnClickListener(v -> requestOtp());

        verifyOtpButton.setOnClickListener(v -> verifyOtp());

        requestOtp();
        startTimer();
    }

    private void requestOtp() {
        if (!email.isEmpty()) {
            String[] field = {"email"};
            String[] data = {email};
            NetworkUtils.postData("https://calestechsync.dermocura.net/calestechsync/sendOTP.php", field, data, result -> {
                if (result.equals("OTP sent")) {
                    Toast.makeText(getApplicationContext(), "OTP sent successfully!", Toast.LENGTH_LONG).show();
                    startTimer();
                } else {
                    error.setText(result);
                }
            });
        } else {
            error.setVisibility(View.VISIBLE);
            error.setText("Email is required!");
        }
    }

    private void verifyOtp() {
        String otp = otpField.getText().toString().trim();

        if (!email.isEmpty() && !otp.isEmpty()) {
            String[] field = {"email", "otp"};
            String[] data = {email, otp};
            NetworkUtils.postData("https://calestechsync.dermocura.net/calestechsync/verifyOTP.php", field, data, result -> {
                if (result.equals("OTP verified")) {
                    error.setVisibility(View.VISIBLE);
                    error.setText("OTP verified successfully!");
                    error.setTextColor(getResources().getColor(R.color.green));
                    register();
                } else {
                    error.setVisibility(View.VISIBLE);
                    error.setText("Failed to verify OTP: " + result);
                }
            });
        } else {
            error.setVisibility(View.VISIBLE);
            error.setText("Email and OTP are required!");
        }
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) { // 1-minute timer
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                timerTextView.setText(String.format("00:%02d", seconds));
                resendOtpButton.setText("Resend code in ");
            }

            @Override
            public void onFinish() {
                timerTextView.setText("00:00");
                resendOtpButton.setText("Resend code");
                resendOtpButton.setEnabled(true);
                error.setVisibility(View.VISIBLE);
                error.setText("You can request a new OTP now.");
            }
        };

        countDownTimer.start();
        resendOtpButton.setEnabled(false);
    }

    private void register() {
        String[] field = {"fullname", "username", "password", "email"};
        String[] data = {name, username, password, email};

        NetworkUtils.postData("https://calestechsync.dermocura.net/calestechsync/signup.php", field, data, result -> {
            if (result.equals("Sign Up Success")) {
                error.setText("Sign Up Success");
                error.setTextColor(getResources().getColor(R.color.green));
                Intent intent = new Intent(getApplicationContext(), gender.class);
                startActivity(intent);
            } else {
                error.setText(result);
            }
        });
    }
}
