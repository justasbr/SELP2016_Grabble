package com.example.justas.grabble.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.justas.grabble.data.SubmittedWordsContract.WordEntry;

public class SubmittedWordsOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "grabble_sqlite.db";
    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_TABLE_CREATE =
            "CREATE TABLE " + WordEntry.TABLE_NAME + " (" +
                    WordEntry.COLUMN_NAME_WORD + " TEXT, " +
                    WordEntry.COLUMN_NAME_SCORE + " INTEGER," +
                    WordEntry.COLUMN_NAME_DATETIME + " DATETIME);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WordEntry.TABLE_NAME + ";";

    public SubmittedWordsOpenHelper(Context context) {
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
