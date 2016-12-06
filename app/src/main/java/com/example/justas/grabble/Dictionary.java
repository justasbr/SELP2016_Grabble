package com.example.justas.grabble;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Dictionary {
    private static Dictionary instance = null;
    private static Context context;
    private static final String className = Dictionary.class.getSimpleName();

    private Set<String> wordMap = new HashSet<>();

    private Dictionary() {
        parseDictionaryFromFile();
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
}
