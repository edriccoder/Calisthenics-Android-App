package com.example.gymapp;

import java.util.List;

public class EMGDuration {
    private boolean success;
    private List<String> emg_durations;
    private String error;

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public List<String> getEmg_durations() {
        return emg_durations;
    }

    public String getError() {
        return error;
    }
}
