package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class signups extends AppCompatActivity {
    Button login, butNext;
    EditText signupName, signupEmail, signupPassword, signupUsername;
    CheckBox termsCheckbox;
    TextView error, termsText;

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
        error = findViewById(R.id.error);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        termsText = findViewById(R.id.termsText);

        // Make the Terms and Conditions text clickable to open a dialog
        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the Terms and Conditions dialog
                TermsAndConditionsDialog dialog = new TermsAndConditionsDialog(signups.this);
                dialog.setCancelable(true);
                dialog.show();
            }
        });

        // Toggle password visibility
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

        // Handle the Sign-up process
        butNext = findViewById(R.id.next);
        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllFieldsFilled()) {
                    String name = String.valueOf(signupName.getText());
                    String email = String.valueOf(signupEmail.getText());
                    String password = String.valueOf(signupPassword.getText());
                    Globals.username = String.valueOf(signupUsername.getText());

                    // Proceed to next activity (OTP Verification)
                    Intent intent = new Intent(signups.this, OtpActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("username", Globals.username);
                    startActivity(intent);
                } else {
                    error.setVisibility(View.VISIBLE);
                    error.setText("All fields required!");
                }
            }
        });

        // Handle login click
        login = findViewById(R.id.button3);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Check if all fields are filled
    private boolean isAllFieldsFilled() {
        return !signupName.getText().toString().isEmpty() &&
                !signupEmail.getText().toString().isEmpty() &&
                !signupPassword.getText().toString().isEmpty() &&
                !signupUsername.getText().toString().isEmpty();
    }
}
