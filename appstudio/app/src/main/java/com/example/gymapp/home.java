package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class home extends Fragment {

    private ListView listViewFocusAreas;
    private FocusAreaAdapter adapter;
    private ArrayList<String> focusAreas;
    private String selectedDifficultyLevel;
    private RecyclerView recyclerViewFocusAreas;
    private WeeklyPlanAdapter adapterWeek;
    private ArrayList<WeeklyPlan> weeklyPlanList;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize ImageView and ListView
        ImageView exercise = view.findViewById(R.id.imageView4);
        listViewFocusAreas = view.findViewById(R.id.listViewFocusAreas);
        focusAreas = new ArrayList<>();
        adapter = new FocusAreaAdapter(getContext(), focusAreas);

        listViewFocusAreas.setAdapter(adapter);

        // Initialize Buttons
        Button buttonBeginner = view.findViewById(R.id.button);
        Button buttonIntermediate = view.findViewById(R.id.button4);
        Button buttonAdvance = view.findViewById(R.id.button5);

        // Set onClickListeners for buttons
        buttonBeginner.setOnClickListener(v -> updateFocusAreas("Beginner", R.drawable.beginner));
        buttonIntermediate.setOnClickListener(v -> updateFocusAreas("Intermediate", R.drawable.inter3));
        buttonAdvance.setOnClickListener(v -> updateFocusAreas("Advance", R.drawable.advance3));

        listViewFocusAreas.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedFocusBody = focusAreas.get(position);
            Intent intent = new Intent(getContext(), FocusBody.class);
            intent.putExtra("focusbody", selectedFocusBody);
            intent.putExtra("exdifficulty", selectedDifficultyLevel);
            startActivity(intent);
        });

        exercise.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), exercise_list.class);
            startActivity(intent);
        });

        // Initialize RecyclerView
        recyclerViewFocusAreas = view.findViewById(R.id.recyclerViewFocusAreas);
        recyclerViewFocusAreas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        weeklyPlanList = new ArrayList<>();
        // Set up the RecyclerView adapter in the home fragment
        adapterWeek = new WeeklyPlanAdapter(getContext(), weeklyPlanList, new WeeklyPlanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String count) {

                Intent intent = new Intent(getContext(), weekly_generate.class);

                intent.putExtra("count", count);

                startActivity(intent);
            }
        });

        recyclerViewFocusAreas.setAdapter(adapterWeek);

        requestQueue = Volley.newRequestQueue(getContext());

        String username = MainActivity.GlobalsLogin.username;

        // Check if the username is not null or empty before proceeding
        if (username != null && !username.isEmpty()) {
            fetchWeeklyPlan(username);
        } else {
            Log.e("Username Error", "Username is null or empty");
        }

        return view;
    }

    private void updateFocusAreas(String level, int imageResId) {
        selectedDifficultyLevel = level;
        focusAreas.clear();

        switch (level) {
            case "Beginner":
            case "Intermediate":
            case "Advance":
                focusAreas.addAll(Arrays.asList("Arms", "Chest", "Abs", "Legs", "Back"));
                break;
            default:
                focusAreas.add("No focus areas available");
                break;
        }

        adapter.setImageResId(imageResId);
        adapter.notifyDataSetChanged();
        listViewFocusAreas.setVisibility(View.VISIBLE);
    }

    private static class FocusAreaAdapter extends ArrayAdapter<String> {

        private int imageResId;

        public FocusAreaAdapter(Context context, ArrayList<String> focusAreas) {
            super(context, 0, focusAreas);
        }

        public void setImageResId(int imageResId) {
            this.imageResId = imageResId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_card, parent, false);
            }

            String focusArea = getItem(position);

            TextView textViewItem = convertView.findViewById(R.id.textViewItem);
            ImageView imageViewTopRight = convertView.findViewById(R.id.imageViewTopRight);
            ImageView imageViewBackground = convertView.findViewById(R.id.background);

            textViewItem.setText(focusArea);
            imageViewTopRight.setBackgroundResource(imageResId);

            int drawableResId;
            switch (focusArea) {
                case "Arms":
                    drawableResId = R.drawable.arms_back;
                    break;
                case "Chest":
                    drawableResId = R.drawable.chest_back;
                    break;
                case "Abs":
                    drawableResId = R.drawable.abs_back;
                    break;
                case "Legs":
                    drawableResId = R.drawable.legs_back;
                    break;
                case "Back":
                    drawableResId = R.drawable.back_back;
                    break;
                default:
                    drawableResId = R.drawable.rectangle_9;
                    break;
            }

            Glide.with(getContext())
                    .load(drawableResId)
                    .override(imageViewBackground.getWidth(), imageViewBackground.getHeight())
                    .into(imageViewBackground);

            return convertView;
        }
    }

    private void fetchWeeklyPlan(String username) {
        String url = "https://calestechsync.dermocura.net/calestechsync/get_weeklyGenerate.php";

        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username); // Ensure this is not null or empty
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            weeklyPlanList.clear();

                            if (response.has("message")) {
                                String message = response.getString("message");
                                Log.e("Response Message", message);
                                return;
                            }

                            JSONArray weeklyPlanArray = response.getJSONArray("weekly_plan");

                            for (int i = 0; i < weeklyPlanArray.length(); i++) {
                                JSONObject jsonObject = weeklyPlanArray.getJSONObject(i);
                                String day = jsonObject.getString("day");
                                String status = jsonObject.getString("status");
                                String count = jsonObject.getString("count");

                                weeklyPlanList.add(new WeeklyPlan(day, status, count));
                            }

                            adapterWeek.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String errorMsg = new String(error.networkResponse.data);
                            Log.e("Error Response", errorMsg);
                        }
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}