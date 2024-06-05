package com.example.gymapp;

public class Exercise {
    private String exerciseName;
    private String description;
    private String imageUrl;
    private String difficulty;


    public Exercise(String exerciseName, String description, String imageUrl, String difficulty) {
        this.exerciseName = exerciseName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
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
}

