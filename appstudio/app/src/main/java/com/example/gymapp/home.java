package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;

public class home extends Fragment {

    private ListView listViewFocusAreas;
    private FocusAreaAdapter adapter;
    private ArrayList<String> focusAreas;
    private String selectedDifficultyLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView exercise = view.findViewById(R.id.imageView4);
        listViewFocusAreas = view.findViewById(R.id.listViewFocusAreas);
        focusAreas = new ArrayList<>();
        adapter = new FocusAreaAdapter(getContext(), focusAreas);

        listViewFocusAreas.setAdapter(adapter);

        Button buttonBeginner = view.findViewById(R.id.button);
        Button buttonIntermediate = view.findViewById(R.id.button4);
        Button buttonAdvance = view.findViewById(R.id.button5);

        // Pass both difficulty level and image resource ID to updateFocusAreas
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

        return view;
    }

    // Update the method to accept an image resource ID as a parameter
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
                    drawableResId = R.drawable.rectangle_9; // Fallback background
                    break;
            }

            Glide.with(getContext())
                    .load(drawableResId)
                    .override(imageViewBackground.getWidth(), imageViewBackground.getHeight()) // Scale down if needed
                    .into(imageViewBackground);

            return convertView;
        }
    }
}
