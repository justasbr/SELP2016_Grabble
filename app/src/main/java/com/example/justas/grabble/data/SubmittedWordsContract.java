package com.example.justas.grabble.data;


import android.provider.BaseColumns;

public final class SubmittedWordsContract {
    private SubmittedWordsContract() {
    }

    public static class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "submitted_words";

        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_SCORE = "score";
        public static final String COLUMN_NAME_DATETIME = "submitted_at";
    }
}

