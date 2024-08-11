package com.example.gymapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

public class home extends Fragment {

    private ListView listViewFocusAreas;
    private FocusAreaAdapter adapter;
    private ArrayList<String> focusAreas;

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

        buttonBeginner.setOnClickListener(v -> updateFocusAreas("Beginner"));
        buttonIntermediate.setOnClickListener(v -> updateFocusAreas("Intermediate"));
        buttonAdvance.setOnClickListener(v -> updateFocusAreas("Advance"));

        return view;
    }

    private void updateFocusAreas(String level) {
        focusAreas.clear(); // Clear the current list

        switch (level) {
            case "Beginner":
                focusAreas.addAll(Arrays.asList("Arms", "Chest", "Abs", "Legs"));
                break;
            case "Intermediate":
                focusAreas.addAll(Arrays.asList("Arms", "Chest", "Abs", "Legs", "Back"));
                break;
            case "Advance":
                focusAreas.addAll(Arrays.asList("Arms", "Chest", "Abs", "Legs", "Back", "Cardio"));
                break;
            default:
                focusAreas.add("No focus areas available");
                break;
        }

        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the ListView
        listViewFocusAreas.setVisibility(View.VISIBLE); // Ensure the ListView is visible
    }

    // Custom Adapter for CardView
    private static class FocusAreaAdapter extends ArrayAdapter<String> {

        public FocusAreaAdapter(Context context, ArrayList<String> focusAreas) {
            super(context, 0, focusAreas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_card, parent, false);
            }

            // Get the data item for this position
            String focusArea = getItem(position);

            // Lookup view for data population
            TextView textViewItem = convertView.findViewById(R.id.textViewItem);

            // Populate the data into the template view using the data object
            textViewItem.setText(focusArea);

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
