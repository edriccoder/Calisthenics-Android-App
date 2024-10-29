package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class home extends Fragment {

    // Existing member variables
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

        // Initialize UI components
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

        // Set ListView item click listener
        listViewFocusAreas.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedFocusBody = focusAreas.get(position);
            Intent intent = new Intent(getContext(), FocusBody.class);
            intent.putExtra("focusbody", selectedFocusBody);
            intent.putExtra("exdifficulty", selectedDifficultyLevel);
            startActivity(intent);
        });

        // Set exercise ImageView click listener
        exercise.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), exercise_list.class);
            startActivity(intent);
        });

        // Initialize RecyclerView for Weekly Plans
        recyclerViewFocusAreas = view.findViewById(R.id.recyclerViewFocusAreas);
        recyclerViewFocusAreas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        weeklyPlanList = new ArrayList<>();
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
            fetchUserLevel(username); // Fetch user level to update button states
        } else {
            Log.e("Username Error", "Username is null or empty");
        }

        return view;
    }

    private void updateFocusAreas(String level, int imageResId) {
        selectedDifficultyLevel = level;
        focusAreas.clear();
        adapter.setImageResId(imageResId);
        adapter.notifyDataSetChanged();

        // Fetch focus areas from the server based on the username
        String username = MainActivity.GlobalsLogin.username;
        if (username != null && !username.isEmpty()) {
            fetchFocusBodies(username);
        } else {
            Log.e("Username Error", "Username is null or empty");
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchFocusBodies(String username) {
        String url = "https://calestechsync.dermocura.net/calestechsync/getFocusBodyByUsername.php"; // Replace with your actual URL

        // Show a loading indicator if desired
        // For example: progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("focusBodies")) {
                            JSONArray focusBodiesArray = jsonResponse.getJSONArray("focusBodies");
                            focusAreas.clear();
                            for (int i = 0; i < focusBodiesArray.length(); i++) {
                                String focusBody = focusBodiesArray.getString(i);
                                focusAreas.add(focusBody);
                            }
                            adapter.notifyDataSetChanged();
                        } else if (jsonResponse.has("error")) {
                            String error = jsonResponse.getString("error");
                            Log.e("Fetch FocusBodies", error);
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Fetch FocusBodies", "Unexpected response format.");
                            Toast.makeText(getContext(), "Unexpected response from server.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Fetch FocusBodies", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(getContext(), "Data parsing error.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("Error Response", errorMsg);
                        Toast.makeText(getContext(), "Server error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Network error occurred.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        // Set a timeout and retry policy if needed
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
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

            // Use Glide to load images efficiently
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
                response -> {
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
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("Error Response", errorMsg);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void fetchUserLevel(String username) {
        String url = "https://calestechsync.dermocura.net/calestechsync/getLevelByUsername.php"; // Replace with your actual URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("level")) {
                            String level = jsonResponse.getString("level");
                            updateButtonStates(level);
                            // Optionally, update UI elements based on level
                        } else if (jsonResponse.has("error")) {
                            String error = jsonResponse.getString("error");
                            Log.e("Fetch Level", error);
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Fetch Level", "Unexpected response format.");
                            Toast.makeText(getContext(), "Unexpected response from server.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Fetch Level", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(getContext(), "Data parsing error.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("Error Response", errorMsg);
                        Toast.makeText(getContext(), "Server error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Network error occurred.", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        // Set a timeout and retry policy if needed
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void updateButtonStates(String selectedLevel) {
        Button buttonBeginner = getView().findViewById(R.id.button);
        Button buttonIntermediate = getView().findViewById(R.id.button4);
        Button buttonAdvance = getView().findViewById(R.id.button5);

        // Clear existing drawables
        buttonBeginner.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        buttonIntermediate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        buttonAdvance.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        // Define the lock drawable
        Drawable lockDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_lock);
        if (lockDrawable != null) {
            lockDrawable.setBounds(0, 0, lockDrawable.getIntrinsicWidth(), lockDrawable.getIntrinsicHeight());
        }

        switch (selectedLevel) {
            case "Beginner":
                // Enable Beginner button without lock
                buttonBeginner.setEnabled(true);
                // Disable Intermediate and Advance with lock
                buttonIntermediate.setEnabled(false);
                buttonAdvance.setEnabled(false);
                if (lockDrawable != null) {
                    buttonIntermediate.setCompoundDrawablesWithIntrinsicBounds(null, null, lockDrawable, null);
                    buttonAdvance.setCompoundDrawablesWithIntrinsicBounds(null, null, lockDrawable, null);
                }
                break;
            case "Intermediate":
                // Enable Beginner and Intermediate buttons without lock
                buttonBeginner.setEnabled(true);
                buttonIntermediate.setEnabled(true);
                // Disable Advance with lock
                buttonAdvance.setEnabled(false);
                if (lockDrawable != null) {
                    buttonAdvance.setCompoundDrawablesWithIntrinsicBounds(null, null, lockDrawable, null);
                }
                break;
            case "Advance":
                // Enable all buttons without lock
                buttonBeginner.setEnabled(true);
                buttonIntermediate.setEnabled(true);
                buttonAdvance.setEnabled(true);
                break;
            default:
                // Enable all buttons without lock in case of unexpected level
                buttonBeginner.setEnabled(true);
                buttonIntermediate.setEnabled(true);
                buttonAdvance.setEnabled(true);
                break;
        }

        // Optionally, adjust button appearance for disabled state
        adjustButtonAppearance(buttonBeginner);
        adjustButtonAppearance(buttonIntermediate);
        adjustButtonAppearance(buttonAdvance);
    }

    private void adjustButtonAppearance(Button button) {
        if (!button.isEnabled()) {
            // Change text color to gray
            button.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        } else {
            // Change text color to primary color (replace with your desired color)
            button.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
        }
    }
}
