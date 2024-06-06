package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class MainActivity extends AppCompatActivity {
    EditText loginUsername, loginPassword;
    Button login, signup, forgotPass;
    TextView error;

    public static class GlobalsLogin {
        public static String username;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginUsername = (EditText) findViewById(R.id.username);
        loginPassword = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.button2);
        signup = (Button) findViewById(R.id.button3);
        forgotPass = (Button) findViewById(R.id.forgot);
        error = findViewById(R.id.error);

        CheckBox passwordToggle = findViewById(R.id.passwordToggle);

        passwordToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Toggle password visibility based on checkbox state
                if (isChecked) {
                    // Show password
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // Hide password
                    loginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), forgotpass.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password;
                GlobalsLogin.username = String.valueOf(loginUsername.getText());
                password = String.valueOf(loginPassword.getText());

                Intent intent = new Intent(getApplicationContext(), home.class);
                intent.putExtra("USERNAME_KEY", GlobalsLogin.username);

                if (!GlobalsLogin.username.isEmpty() && !password.isEmpty()) {

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "password";

                            String[] data = new String[2];
                            data[0] = GlobalsLogin.username;
                            data[1] = password;
                            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if(result.equals("Login Success")){
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), homepage.class);
                                        intent.putExtra("USERNAME_KEY", GlobalsLogin.username);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        error.setVisibility(View.VISIBLE);
                                        error.setText(result);
                                    }

                                }
                            }
                        }
                    });
                } else {
                    error.setVisibility(View.VISIBLE);
                    error.setText("All fields required!");
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), signups.class);
                startActivity(intent);
            }
        });
    }
}


