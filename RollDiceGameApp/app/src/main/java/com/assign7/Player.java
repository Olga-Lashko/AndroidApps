package com.assign7;

import android.util.Log;

public class Player {
    private String name;
    private int score;
    private int doubles;
    private int triples;

    public Player(String name, int score, int doubles, int triples) {
        this.name = name;
        this.score = score;
        this.doubles = doubles;
        this.triples = triples;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDoubles() {
         Log.d("Player", name + " Num Doubles: " + doubles);
        return doubles;
    }

    public void setDoubles(int doubles) {
        this.doubles = doubles;
    }

    public int getTriples() {
        return triples;
    }

    public void setTriples(int triples) {
        this.triples = triples;
    }
}

