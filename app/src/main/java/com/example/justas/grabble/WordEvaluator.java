package com.example.justas.grabble;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

//TODO think about architecture of this class

public class WordEvaluator {
    private HashMap<Character, Integer> charScores;
    private SharedPreferences sharedPrefs;
    private Context context;

    public WordEvaluator(Context ctx) {
        charScores = getCharScoreMap();
        context = ctx;
        sharedPrefs = context.getSharedPreferences(
                context.getString(R.string.inventory_file), Context.MODE_PRIVATE);
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

    public void decrementLetters(String word) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Map<Character, Integer> charCount = charOccurences(word);
        for (char c : charCount.keySet()) {

            String letterLabel = String.valueOf(c);
            int possessed = sharedPrefs.getInt(letterLabel, 0);
            int used = charCount.get(c);

            int updatedCount = possessed - used;

            editor.putInt(letterLabel, updatedCount);
        }

        editor.commit();
    }

    public int numberOfLettersOwned() {
        int total = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            String label = String.valueOf(c);
            int count = sharedPrefs.getInt(label, 0);

            total += count;
        }
        return total;
    }

    public boolean hasLettersFor(String word) {
        Map<Character, Integer> charCount = charOccurences(word);
        for (char c : charCount.keySet()) {
            int possessed = sharedPrefs.getInt(String.valueOf(c), 0);
            int needForWord = charCount.get(c);

            if (possessed < needForWord) {
                return false;
            }
        }
        return true;
    }

    private Map<Character, Integer> charOccurences(String word) {
        Map<Character, Integer> occCount = new HashMap<>();
        for (char c : word.toCharArray()) {
            int count = occCount.containsKey(c) ? occCount.get(c) : 0;
            int updatedCount = count + 1;
            occCount.put(c, updatedCount);
        }
        return occCount;
    }
}