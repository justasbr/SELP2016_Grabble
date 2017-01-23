package com.example.justas.grabble;

import android.content.Context;
import android.util.Log;

import com.example.justas.grabble.helper.ScoredWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Dictionary {
    private static Dictionary instance = null;
    private static Context context;
    private static final String className = Dictionary.class.getSimpleName();

    private Set<String> wordMap = new HashSet<>();
    private WordEvaluator wordEvaluator;
    private ArrayList<ScoredWord> scoredWords;

    private Dictionary() {
        parseDictionaryFromFile();
        wordEvaluator = new WordEvaluator(context);
    }

    public boolean containsWord(String word) {
        return wordMap.contains(word);
    }

    private void parseDictionaryFromFile() {
        InputStreamReader isr;
        BufferedReader reader;

        isr = new InputStreamReader(context.getResources().openRawResource(R.raw.grabble_dictionary));
        reader = new BufferedReader(isr);

        try {
            String line = reader.readLine();
            while (line != null) {
                wordMap.add(line.toUpperCase());
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(className, "IOException while parsing dictionary from text file.");
        }
    }

    public static Dictionary getInstance(Context ctx) {
        if (instance == null) {
            context = ctx;
            instance = new Dictionary();
        }
        return instance;
    }

    public ScoredWord getSuggestion() {
        if (scoredWords == null) {
            initScoredWords();
        }

        if (wordEvaluator.numberOfLettersOwned() < 7) {
            Log.d("NUM_OF_LETTERS", String.valueOf(wordEvaluator.numberOfLettersOwned()));
            return null;
        }

        for (ScoredWord scoredWord : scoredWords) {
            if (wordEvaluator.hasLettersFor(scoredWord.word)) {
                return scoredWord;
            }
        }
        return null;
    }

    private synchronized void initScoredWords() {
        scoredWords = new ArrayList<>();

        for (String word : wordMap) {
            int score = wordEvaluator.wordScoreOf(word);

            scoredWords.add(new ScoredWord(word, score));
        }

        //Descending order (in terms of word's value in points)
        Collections.sort(scoredWords, Collections.<ScoredWord>reverseOrder());
    }
}
