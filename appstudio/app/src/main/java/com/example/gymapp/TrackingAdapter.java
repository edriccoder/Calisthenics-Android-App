package com.example.gymapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class TrackingAdapter extends BaseAdapter {
    private Context context;
    private List<ExerciseTracking> exercises;

    public TrackingAdapter(Context context, List<ExerciseTracking> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @Override
    public Object getItem(int position) {
        return exercises.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_tracking, parent, false);
        }

        ExerciseTracking exercise = exercises.get(position);

        ImageView exerciseImage = convertView.findViewById(R.id.exerciseImage);
        TextView exerciseName = convertView.findViewById(R.id.exerciseName);

        exerciseName.setText(exercise.getExname());

        Glide.with(context)
                .load(exercise.getEximg())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(exerciseImage);

        return convertView;
    }
}
