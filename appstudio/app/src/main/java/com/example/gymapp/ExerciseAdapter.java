package com.example.gymapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private ArrayList<Exercise2> exerciseList;

    public ExerciseAdapter(Context context, ArrayList<Exercise2> exercises) {
        super(context, 0, exercises);
        this.exerciseList = exercises;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_exercise, parent, false);
        }

        Exercise2 exercise = getItem(position);

        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewDesc = convertView.findViewById(R.id.textViewDesc);
        TextView textViewActivity = convertView.findViewById(R.id.textViewActivity);
        ImageView imageViewExercise = convertView.findViewById(R.id.imageViewExercise);

        if (exercise != null) {
            textViewName.setText(exercise.getExName() != null ? exercise.getExName() : "No name available");
            textViewDesc.setText(exercise.getExDesc() != null ? exercise.getExDesc() : "No description available");

            String activityText = (exercise.getActivity() == null || exercise.getActivity().isEmpty())
                    ? "Activity: Not specified"
                    : "Activity: " + exercise.getActivity();
            textViewActivity.setText(activityText);

            // Load the local image if available, otherwise load from URL
            if (exercise.getLocalImagePath() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(exercise.getLocalImagePath());
                imageViewExercise.setImageBitmap(bitmap);
            } else if (exercise.getImageUrl() != null && !exercise.getImageUrl().isEmpty()) {
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

            // Set onClickListener to navigate to ExerciseDetailActivity with exercise data
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ExerciseDetailActivity.class);
                intent.putExtra("exerciseList", exerciseList);
                intent.putExtra("currentPosition", position);
                getContext().startActivity(intent);
            });
        }

        return convertView;
    }
}
