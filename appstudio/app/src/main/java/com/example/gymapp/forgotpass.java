package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class forgotpass extends AppCompatActivity {
    EditText email, newPass;
    Button forgot, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpass);

        email = (EditText) findViewById(R.id.email);
        newPass = (EditText) findViewById(R.id.newpass);
        forgot = (Button) findViewById(R.id.forgot);
        login = (Button) findViewById(R.id.login);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emails, newPassword;
                emails = String.valueOf(email.getText());
                newPassword = String.valueOf(newPass.getText());

                if (!emails.isEmpty() && !newPassword.isEmpty()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String[] field = new String[2];
                            field[0] = "email";
                            field[1] = "newPassword";

                            String[] data = new String[2];
                            data[0] = emails;
                            data[1] = newPassword;
                            PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/updatePasswordByEmail.php/", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    String result = putData.getResult();
                                    if(result.equals("Password updated successfully")){
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(forgotpass.this, "All fields required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}