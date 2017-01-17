package com.example.justas.grabble.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.justas.grabble.data.CollectedMarkersContract.MarkerEntry;

public class CollectedMarkersOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "grabble_sqlite.db";
    private static final int DATABASE_VERSION = 9;

    private static final String DATABASE_TABLE_CREATE =
            "CREATE TABLE " + MarkerEntry.TABLE_NAME + " (" +
                    MarkerEntry.COLUMN_NAME_ID + " INTEGER primary key autoincrement, " +
                    MarkerEntry.COLUMN_NAME_LETTER + " TEXT, " +
                    MarkerEntry.COLUMN_NAME_LAT + " DOUBLE, " +
                    MarkerEntry.COLUMN_NAME_LNG + " DOUBLE," +
                    MarkerEntry.COLUMN_NAME_DATETIME + " DATETIME);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MarkerEntry.TABLE_NAME + ";";

    public CollectedMarkersOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d("SQL", "COLLECTED UPGRADE");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
