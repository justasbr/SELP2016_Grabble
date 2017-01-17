package com.example.justas.grabble.data;


import android.provider.BaseColumns;

public final class CollectedMarkersContract {
    private CollectedMarkersContract() {
    }

    public static class MarkerEntry implements BaseColumns {
        public static final String TABLE_NAME = "collected_markers";

        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_LETTER = "letter";
        public static final String COLUMN_NAME_LAT = "lat";
        public static final String COLUMN_NAME_LNG = "lng";
        public static final String COLUMN_NAME_DATETIME = "collected_at";
    }
}

