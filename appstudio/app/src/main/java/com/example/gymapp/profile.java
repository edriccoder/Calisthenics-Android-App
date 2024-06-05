package com.example.gymapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class profile extends Fragment {
    TextView name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile2, container, false);

        name = view.findViewById(R.id.name);

        String username = getActivity().getIntent().getStringExtra("USERNAME_KEY");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String user = username;

                String[] field = new String[1];
                field[0] = "username";

                String[] data = new String[1];
                data[0] = user;

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getNameByUsername.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        if (!result.equals("Username not found")) {
                            name.setText(result);
                        } else {
                            Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        return view;
    }
}