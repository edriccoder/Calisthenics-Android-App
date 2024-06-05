package com.example.gymapp;

public class Exercise_Generate {
    private String name;
    private String description;
    private String image;
    private String difficulty;
    private String focusBody;

    public Exercise_Generate(String name, String description, String image, String difficulty, String focusBody) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.difficulty = difficulty;
        this.focusBody = focusBody;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getFocusBody() {
        return focusBody;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setFocusBody(String focusBody) {
        this.focusBody = focusBody;
    }
}
