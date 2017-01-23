package com.example.justas.grabble.helper;

public class Player {
    private String name;
    private String id;
    private int totalPoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Player() {
    }

    public Player(String name, int totalPoints) {
        this.name = name;
        this.totalPoints = totalPoints;
    }
}
