package com.example.gymapp;

public class ExerciseFocus {

    private String name;
    private String description;
    private String imageUrl;
    private String difficulty;

    public ExerciseFocus(String name, String description, String imageUrl, String difficulty) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
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

