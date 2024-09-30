package com.example.gymapp;

public class WeeklyPlan {
    private String day;
    private String status;
    private String count;

    public WeeklyPlan(String day, String status, String count) {
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

    public String getCount() { return count; }
}
