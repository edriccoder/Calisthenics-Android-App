package com.example.gymapp;

// Exercise.java
public class Exercise_Warm {
    private String exname;
    private String exdesc;
    private String eximg;
    private String lossWeight;

    public Exercise_Warm(String exname, String exdesc, String eximg, String lossWeight) {
        this.exname = exname;
        this.exdesc = exdesc;
        this.eximg = eximg;
        this.lossWeight = lossWeight;
    }

    public String getExname() {
        return exname;
    }

    public String getExdesc() {
        return exdesc;
    }

    public String getEximg() {
        return eximg;
    }

    public String getLossWeight() {
        return lossWeight;
    }
}

