package com.example.justas.grabble.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectedMarkersOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CollectedMarkers.db";

    private static final String KEY_LETTER = "letter";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_DATETIME = "collection_datetime";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE_NAME = "collected_markers";
    private static final String DATABASE_TABLE_CREATE =
            "CREATE TABLE " + DATABASE_TABLE_NAME + " (" +
                    KEY_LETTER + " LETTER, " +
                    KEY_LAT + " DOUBLE, " +
                    KEY_LNG + " INTEGER," +
                    KEY_DATETIME + "DATETIME PRIMARY KEY);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME;

    CollectedMarkersOpenHelper(Context context) {
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
