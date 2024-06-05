package com.example.gymapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class setting extends Fragment {
    Button logout;
    Button feedback, bluetooth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        logout = (Button) view.findViewById(R.id.logout);
        feedback = (Button) view.findViewById(R.id.feedback);
        bluetooth = (Button) view.findViewById(R.id.bluetooth);

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), bluetooth.class);
                startActivity(intent);

            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the feedback link in the user's default web browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://calestechsync.dermocura.net/feedback.php"));
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String url = "https://calestechsync.dermocura.net/calestechsync/logout.php";
                        RequestQueue queue = Volley.newRequestQueue(getContext());

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getContext(), startup.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getContext(), "Logout failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        queue.add(stringRequest);
                    }
                });

            }
        });

        return view;
    }
}