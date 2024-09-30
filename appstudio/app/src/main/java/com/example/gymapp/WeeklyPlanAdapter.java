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
    private OnItemClickListener listener;

    // Constructor modified to accept a listener
    public WeeklyPlanAdapter(Context context, ArrayList<WeeklyPlan> list, OnItemClickListener listener) {
        this.context = context;
        this.weeklyPlanList = list;
        this.listener = listener;
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

        // Set click listener to pass the count to the activity
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(weeklyPlan.getCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return weeklyPlanList.size();
    }

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(String count);
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

