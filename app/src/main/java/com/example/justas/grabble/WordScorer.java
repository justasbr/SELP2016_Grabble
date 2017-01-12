package com.example.justas.grabble;

import java.util.HashMap;

public class WordScorer {
    private HashMap<Character, Integer> charScores;

    public WordScorer() {
        charScores = getCharScoreMap();
    }

    public int wordScoreOf(String word) {
        int score = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            score += charScoreOf(c);
        }
        return score;
    }

    private HashMap<Character, Integer> getCharScoreMap() {
        HashMap<Character, Integer> charScores = new HashMap<>();

        charScores.put('a', 3);
        charScores.put('b', 20);
        charScores.put('c', 13);
        charScores.put('d', 10);
        charScores.put('e', 1);
        charScores.put('f', 15);
        charScores.put('g', 18);
        charScores.put('h', 9);
        charScores.put('i', 5);
        charScores.put('j', 25);
        charScores.put('k', 22);
        charScores.put('l', 11);
        charScores.put('m', 14);
        charScores.put('n', 6);
        charScores.put('o', 4);
        charScores.put('p', 19);
        charScores.put('q', 24);
        charScores.put('r', 8);
        charScores.put('s', 7);
        charScores.put('t', 2);
        charScores.put('u', 12);
        charScores.put('v', 21);
        charScores.put('w', 17);
        charScores.put('x', 23);
        charScores.put('y', 16);
        charScores.put('z', 26);

        return charScores;
    }

    private int charScoreOf(char c) {
        c = Character.toLowerCase(c);

        if (charScores.containsKey(c)) {
            return charScores.get(c);
        } else {
            return 0;
        }
    }
}