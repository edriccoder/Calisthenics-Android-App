package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;
import java.util.List;

public class exercise_adapter extends ArrayAdapter<Exercise> {
    private List<Exercise> exercises;

    public exercise_adapter(Context context, List<Exercise> exercises) {
        super(context, 0, exercises);
        this.exercises = new ArrayList<>(exercises);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Exercise exercise = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView exerciseNameTextView = convertView.findViewById(R.id.exerciseNameTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView difficultyTextView = convertView.findViewById(R.id.difficultyTextView);

        exerciseNameTextView.setText(exercise.getExerciseName());
        if (!exercise.getImageUrl().isEmpty()) {

            if (exercise.getImageUrl().endsWith(".gif")) {

                Glide.with(getContext()).asGif().load(exercise.getImageUrl()).into(imageView);
            } else {

                Glide.with(getContext())
                        .load(exercise.getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView);
            }
        } else {

            imageView.setImageResource(R.drawable.dumbell);
        }
        difficultyTextView.setText("Difficulty: " + exercise.getDifficulty());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exercise exercise = getItem(position);
                Intent intent = new Intent(getContext(), exercise_details.class);
                intent.putExtra("exerciseName", exercise.getExerciseName());
                intent.putExtra("description", exercise.getDescription());
                intent.putExtra("imageUrl", exercise.getImageUrl());
                intent.putExtra("difficulty", exercise.getDifficulty());
                getContext().startActivity(intent);
            }
        });

        return convertView;

    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public Exercise getItem(int position) {
        return exercises.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void filterList(ArrayList<Exercise> filteredList) {
        exercises = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }
}
