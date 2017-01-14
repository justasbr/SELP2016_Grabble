package com.example.justas.grabble.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SubmittedWordsOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SubmittedWords.db";

    private static final String KEY_WORD = "word";
    private static final String KEY_SCORE = "score";
    private static final String KEY_DATETIME = "submission_datetime";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE_NAME = "submitted_words";
    private static final String DATABASE_TABLE_CREATE =
            "CREATE TABLE " + DATABASE_TABLE_NAME + " (" +
                    KEY_WORD + " TEXT, " +
                    KEY_SCORE + " INTEGER," +
                    KEY_DATETIME + "DATETIME PRIMARY KEY);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME;

    SubmittedWordsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
