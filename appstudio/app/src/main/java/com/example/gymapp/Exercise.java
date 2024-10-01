package com.example.gymapp;

public class Exercise {
    private String exerciseName;
    private String description;
    private String imageUrl;
    private String difficulty;
    private String focusbody;
    private String BuildMuscle;


    public Exercise(String exerciseName, String description, String imageUrl, String difficulty, String focusbody, String BuildMuscle) {
        this.exerciseName = exerciseName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
        this.focusbody = focusbody;
        this.BuildMuscle = BuildMuscle;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getFocusbody() {
        return focusbody;
    }

    public String getBuildMuscle() {
        return BuildMuscle;
    }
}

