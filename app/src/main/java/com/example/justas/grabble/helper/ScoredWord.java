package com.example.justas.grabble.helper;

public class ScoredWord implements Comparable<ScoredWord> {
    public String word;
    public int score = -1;

    public ScoredWord(String w, int s) {
        word = w;
        score = s;
    }

    public String prettyPrint() {
        if (word != null && score >= 0) {
            return word + " (" + score + ")";
        }
        return "-";
    }


    @Override
    public int compareTo(ScoredWord other) {
        return this.score - other.score;
    }
}