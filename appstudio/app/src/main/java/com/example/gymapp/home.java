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

        listViewFocusAreas = view.findViewById(R.id.listViewFocusAreas);
        focusAreas = new ArrayList<>();
        adapter = new FocusAreaAdapter(getContext(), focusAreas);

        listViewFocusAreas.setAdapter(adapter);

        Button buttonBeginner = view.findViewById(R.id.button);
        Button buttonIntermediate = view.findViewById(R.id.button4);
        Button buttonAdvance = view.findViewById(R.id.button5);

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

            textViewItem.setText(focusArea);
            imageViewTopRight.setBackgroundResource(imageResId);

            return convertView;
        }
    }
}
