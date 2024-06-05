package com.example.gymapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class home extends Fragment {
    ListView listView;
    ImageButton imageButton;
    CardView cardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imageButton = view.findViewById(R.id.dumbell);
        listView = view.findViewById(R.id.listView);
        cardView = view.findViewById(R.id.cardView2);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), exercise_list.class);
                startActivity(intent);
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), warmup.class);
                intent.putExtra("TITLE_EXTRA", "Warm Up");
                startActivity(intent);
            }
        });


        String username = getActivity().getIntent().getStringExtra("USERNAME_KEY");
        fetchWeeklyGoals(username);

        return view;
    }

    private void fetchWeeklyGoals(String username) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String[] field = {"username"};
                String[] data = {username};

                PutData putData = new PutData("https://calestechsync.dermocura.net/calestechsync/getWeeklyGoalandFocusGoalByUsername.php", "POST", field, data);
                if (putData.startPut()) {
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        ArrayList<WeeklyGoal> weeklyGoalsList = parseWeeklyGoals(result);

                        if (weeklyGoalsList != null && !weeklyGoalsList.isEmpty()) {
                            Day_Adapter adapter = new Day_Adapter(getContext(), weeklyGoalsList);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "No weekly goals found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error completing request", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Error sending request", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<WeeklyGoal> parseWeeklyGoals(String result) {
        ArrayList<WeeklyGoal> weeklyGoalsList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String day = jsonObject.optString("day", "");
                String focusBody = jsonObject.optString("focusbody", "");
                WeeklyGoal weeklyGoal = new WeeklyGoal(day, focusBody);
                weeklyGoalsList.add(weeklyGoal);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error parsing weekly goals data", Toast.LENGTH_SHORT).show();
        }

        return weeklyGoalsList;
    }
}
