package com.example.gymapp;

public class WeeklyPlan {
    private String day;
    private String status;
    private int count;

    public WeeklyPlan(String day, String status, int count) {
        this.day = day;
        this.status = status;
        this.count = count;
    }

    public String getDay() {
        return day;
    }

    public String getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }
}
