package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

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

        resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOtp();
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        requestOtp();
        startTimer();
    }

    private void requestOtp() {
        if (!email.isEmpty()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[1];
                    field[0] = "email";
                    String[] data = new String[1];
                    data[0] = email;
                    PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/sendOTP.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            if (result.equals("OTP sent")) {
                                Toast.makeText(getApplicationContext(), "OTP sent successfully!", Toast.LENGTH_LONG).show();
                                startTimer();
                            } else {
                                error.setText(result);
                            }
                        }
                    }
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
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "email";
                    field[1] = "otp";
                    String[] data = new String[2];
                    data[0] = email;
                    data[1] = otp;
                    PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/verifyOTP.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            if (result.equals("OTP verified")) {
                                error.setVisibility(View.VISIBLE);
                                error.setText("OTP verified successfully!");
                                error.setTextColor(getResources().getColor(R.color.green)); // Assuming you have a color resource named "green" defined in your colors.xml file
                                register();
                            } else {
                                error.setVisibility(View.VISIBLE);
                                error.setText("Failed to verify OTP: " + result);
                            }
                        }
                    }
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

        countDownTimer = new CountDownTimer(300000, 1000) { // 5 minutes timer
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
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
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = new String[4];
                field[0] = "fullname";
                field[1] = "username";
                field[2] = "password";
                field[3] = "email";
                String[] data = new String[4];
                data[0] = name;
                data[1] = username;
                data[2] = password;
                data[3] = email;
                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/signup.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (result.equals("Sign Up Success")) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), gender.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed: " + result, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error completing registration.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error starting registration.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
