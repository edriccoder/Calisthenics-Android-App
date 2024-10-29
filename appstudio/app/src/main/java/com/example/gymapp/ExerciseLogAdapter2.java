package com.example.gymapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ExerciseLogAdapter2 extends ArrayAdapter<ExerciseLog> {
    public ExerciseLogAdapter2(Context context, List<ExerciseLog> logs) {
        super(context, 0, logs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.exercise_log_item, parent, false);
        }

        ExerciseLog log = getItem(position);

        // Assuming you have TextViews in exercise_log_item.xml
        TextView exerciseNameView = convertView.findViewById(R.id.exerciseName);
        TextView setsRepsView = convertView.findViewById(R.id.setsReps);

        if (log != null) {
            exerciseNameView.setText(log.getExerciseName());
            setsRepsView.setText("Sets: " + log.getSets() + ", Reps: " + log.getReps());
        }

        return convertView;
    }
}
