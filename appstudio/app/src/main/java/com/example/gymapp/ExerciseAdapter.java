package com.example.gymapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.ArrayList;

public class ExerciseAdapter extends ArrayAdapter<Exercise2> {

    public ExerciseAdapter(Context context, ArrayList<Exercise2> exercises) {
        super(context, 0, exercises);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_exercise, parent, false);
        }

        Exercise2 exercise = getItem(position);

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewDesc = convertView.findViewById(R.id.textViewDesc);
        ImageView imageViewExercise = convertView.findViewById(R.id.imageViewExercise);

        textViewName.setText(exercise.getExName());
        textViewDesc.setText(exercise.getExDesc());

        if (!exercise.getImageUrl().isEmpty()) {
            if (exercise.getImageUrl().endsWith(".gif")) {
                Glide.with(getContext())
                        .asGif()
                        .load(exercise.getImageUrl())
                        .into(imageViewExercise);
            } else {
                Glide.with(getContext())
                        .load(exercise.getImageUrl())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageViewExercise);
            }
        } else {
            imageViewExercise.setImageResource(R.drawable.dumbell);
        }

        return convertView;
    }
}
