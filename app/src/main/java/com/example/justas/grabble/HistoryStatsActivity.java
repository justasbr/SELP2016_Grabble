package com.example.justas.grabble;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.justas.grabble.data.SubmittedWordsContract.WordEntry;
import com.example.justas.grabble.data.SubmittedWordsOpenHelper;

public class HistoryStatsActivity extends AppCompatActivity {
    private ListView mSubmittedWordsListView;

    private SubmittedWordsOpenHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_stats);

        mDbHelper = new SubmittedWordsOpenHelper(getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        mSubmittedWordsListView = (ListView) findViewById(R.id.submitted_words_listview);

        int nSubmitted = fetchWordCount();
        TextView submittedTextView = (TextView) findViewById(R.id.stats_total_words);
        submittedTextView.setText(String.valueOf(nSubmitted));

        int nPoints = fetchTotalPoints();
        TextView pointsTextView = (TextView) findViewById(R.id.stats_total_points);
        pointsTextView.setText(String.valueOf(nPoints));

        String pointsPerWord = nSubmitted == 0 ? "N/A" : String.valueOf(nPoints / nSubmitted);
        TextView pointsPerWordTextView = (TextView) findViewById(R.id.stats_total_ppw);
        pointsPerWordTextView.setText(String.valueOf(pointsPerWord));

        ScoredWord bestWord = getBestWord();
        TextView bestWordTextView = (TextView) findViewById(R.id.stats_best_word);
        bestWordTextView.setText(bestWord.prettyPrint());

        Cursor cursor = fetchAllSubmittedWords();
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.submitted_words_list_item, cursor,
                new String[]{WordEntry.COLUMN_NAME_WORD, WordEntry.COLUMN_NAME_SCORE, WordEntry.COLUMN_NAME_DATETIME},
                new int[]{R.id.submitted_word_item_word, R.id.submitted_word_item_score, R.id.submitted_word_item_datetime},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        mSubmittedWordsListView.setAdapter(cursorAdapter);
    }

    private Cursor fetchAllSubmittedWords() {
        return db.query(WordEntry.TABLE_NAME, null, null, null, null, null, null);
    }

    private int fetchWordCount() {
        Cursor c = db.rawQuery("select count(*) from " + WordEntry.TABLE_NAME, null);
        c.moveToFirst();
        int wordCount = c.getInt(0);
        c.close();
        return wordCount;
    }

    private int fetchTotalPoints() {
        int totalPoints = 0;
        Cursor c = db.rawQuery("select sum(" + WordEntry.COLUMN_NAME_SCORE + ") from " + WordEntry.TABLE_NAME, null);
        if (c.moveToFirst()) {
            totalPoints = c.getInt(0);
        }
        c.close();
        return totalPoints;
    }

    public ScoredWord getBestWord() {
        ScoredWord bestWord = null;
        Cursor c = db.query(WordEntry.TABLE_NAME, null, WordEntry.COLUMN_NAME_SCORE + "=(select max(" + WordEntry.COLUMN_NAME_SCORE + ") from " + WordEntry.TABLE_NAME + ")", null, null, null, null);
        if (c.moveToFirst()) {

            String word = c.getString(c.getColumnIndex(WordEntry.COLUMN_NAME_WORD));
            int score = c.getInt(c.getColumnIndex(WordEntry.COLUMN_NAME_SCORE));

            bestWord = new ScoredWord(word, score);
        }
        c.close();
        return bestWord;
    }
}