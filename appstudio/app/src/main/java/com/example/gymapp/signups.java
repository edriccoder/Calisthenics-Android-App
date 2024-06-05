package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.regex.Pattern;

public class signups extends AppCompatActivity {
    Button login, butNext;
    Button requestOtpButton, verifyOtpButton;
    EditText signupName, signupEmail, signupPassword, signupUsername, otpField;

    boolean isOtpVerified = false;

    public static class Globals {
        public static String username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signups);

        signupName = findViewById(R.id.fullname);
        signupEmail = findViewById(R.id.email);
        signupPassword = findViewById(R.id.password);
        signupUsername = findViewById(R.id.username);
        otpField = findViewById(R.id.otp);

        CheckBox passwordToggle = findViewById(R.id.passwordToggle);
        passwordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    signupPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    signupPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        requestOtpButton = findViewById(R.id.requestOtp);
        requestOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestOtp();
            }
        });

        verifyOtpButton = findViewById(R.id.verifyOtp);
        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOtp();
            }
        });

        butNext = findViewById(R.id.next);
        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOtpVerified) {
                    registerUser();
                } else {
                    Toast.makeText(signups.this, "Please verify OTP first!", Toast.LENGTH_LONG).show();
                }
            }
        });

        login = findViewById(R.id.button3);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestOtp() {
        String email = String.valueOf(signupEmail.getText());
        if (!email.isEmpty()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[1];
                    field[0] = "email";
                    String[] data = new String[1];
                    data[0] = email;
                    PutData putData = new PutData("http://192.168.1.28/calestechsync/sendOTP.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            if (result.equals("OTP sent")) {
                                Toast.makeText(getApplicationContext(), "OTP sent successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to send OTP: " + result, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error completing OTP request.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error starting OTP request.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(signups.this, "Email is required!", Toast.LENGTH_LONG).show();
        }
    }

    private void verifyOtp() {
        String email = String.valueOf(signupEmail.getText());
        String otp = String.valueOf(otpField.getText());

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
                    PutData putData = new PutData("http://192.168.1.28/calestechsync/verifyOTP.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            if (result.equals("OTP verified")) {
                                isOtpVerified = true;
                                Toast.makeText(getApplicationContext(), "OTP verified successfully!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to verify OTP: " + result, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error completing OTP verification.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error starting OTP verification.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(signups.this, "Email and OTP are required!", Toast.LENGTH_LONG).show();
        }
    }

    private void registerUser() {
        String name = String.valueOf(signupName.getText());
        String email = String.valueOf(signupEmail.getText());
        String password = String.valueOf(signupPassword.getText());
        String username = String.valueOf(signupUsername.getText());

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !username.isEmpty()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[4];
                    field[0] = "name";
                    field[1] = "email";
                    field[2] = "password";
                    field[3] = "username";
                    String[] data = new String[4];
                    data[0] = name;
                    data[1] = email;
                    data[2] = password;
                    data[3] = username;
                    PutData putData = new PutData("http://192.168.1.28/calestechsync/signup.php", "POST", field, data);
                    if (putData.startPut()) {
                        if (putData.onComplete()) {
                            String result = putData.getResult();
                            if (result.equals("Sign Up Success")) {
                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        } else {
            Toast.makeText(signups.this, "All fields are required!", Toast.LENGTH_LONG).show();
        }
    }
}