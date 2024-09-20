package com.example.gymapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ExerciseLogAdapter extends BaseAdapter {

    private Context context;
    private List<ExerciseLog> exerciseLogList;

    public ExerciseLogAdapter(Context context, List<ExerciseLog> exerciseLogList) {
        this.context = context;
        this.exerciseLogList = exerciseLogList;
    }

    @Override
    public int getCount() {
        return exerciseLogList.size();
    }

    @Override
    public Object getItem(int position) {
        return exerciseLogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.exercise_log_item, parent, false);
        }

        ExerciseLog exerciseLog = exerciseLogList.get(position);

        TextView exerciseName = convertView.findViewById(R.id.exerciseName);
        TextView setsReps = convertView.findViewById(R.id.setsReps);
        TextView logDate = convertView.findViewById(R.id.logDate);

        exerciseName.setText(exerciseLog.getExerciseName());
        setsReps.setText("Sets: " + exerciseLog.getSets() + ", Reps: " + exerciseLog.getReps());
        logDate.setText(exerciseLog.getLogDate());

        return convertView;
    }
}
