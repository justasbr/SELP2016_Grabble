package com.example.justas.grabble.helper;

public class WordSubmission {
    private String id;
    private String word;

    public WordSubmission(String word, String id) {
        this.id = id;
        this.word = word;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
