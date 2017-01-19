package com.example.justas.grabble;

public class ScoredWord {
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
}