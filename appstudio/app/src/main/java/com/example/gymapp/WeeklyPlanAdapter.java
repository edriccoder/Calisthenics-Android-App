package com.example.gymapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.WeeklyPlan;

import java.util.ArrayList;

public class WeeklyPlanAdapter extends RecyclerView.Adapter<WeeklyPlanAdapter.WeeklyPlanViewHolder> {

    private ArrayList<WeeklyPlan> weeklyPlanList;
    private Context context;

    public WeeklyPlanAdapter(Context context, ArrayList<WeeklyPlan> list) {
        this.context = context;
        this.weeklyPlanList = list;
    }

    @NonNull
    @Override
    public WeeklyPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weekly_plan, parent, false);
        return new WeeklyPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyPlanViewHolder holder, int position) {
        WeeklyPlan weeklyPlan = weeklyPlanList.get(position);
        holder.dayTextView.setText(weeklyPlan.getDay());
        holder.statusTextView.setText(weeklyPlan.getStatus());
    }

    @Override
    public int getItemCount() {
        return weeklyPlanList.size();
    }

    public static class WeeklyPlanViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView, statusTextView;

        public WeeklyPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.dayTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
        }
    }
}
