package com.example.justas.grabble;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.justas.grabble.data.SubmittedWordsContract.WordEntry;
import com.example.justas.grabble.data.SubmittedWordsOpenHelper;
import com.example.justas.grabble.helper.ScoredWord;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

        if (nSubmitted > 0) {
            String pointsPerWord = String.valueOf(nPoints / nSubmitted);
            TextView pointsPerWordTextView = (TextView) findViewById(R.id.stats_total_ppw);
            pointsPerWordTextView.setText(String.valueOf(pointsPerWord));
        }

        ScoredWord bestWord = getBestWord();
        if (bestWord != null) {
            TextView bestWordTextView = (TextView) findViewById(R.id.stats_best_word);
            bestWordTextView.setText(bestWord.prettyPrint());
        }

        Cursor cursor = fetchAllSubmittedWords();

        CursorAdapter cursorAdapter = new CursorAdapter(getApplicationContext(), cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context).inflate(R.layout.submitted_words_list_item, viewGroup, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView wordView = (TextView) view.findViewById(R.id.submitted_word_item_word);
                TextView scoreView = (TextView) view.findViewById(R.id.submitted_word_item_score);
                TextView dateView = (TextView) view.findViewById(R.id.submitted_word_item_datetime);

                String word = cursor.getString(cursor.getColumnIndex(WordEntry.COLUMN_NAME_WORD));
                String score = cursor.getString(cursor.getColumnIndex(WordEntry.COLUMN_NAME_SCORE));
                String date = cursor.getString(cursor.getColumnIndex(WordEntry.COLUMN_NAME_DATETIME));
                String userFriendlyDate = getUserFriendlyDate(date);

                wordView.setText(word);
                scoreView.setText(score);
                dateView.setText(userFriendlyDate);
            }
        };

        mSubmittedWordsListView.setAdapter(cursorAdapter);
    }

    private String getUserFriendlyDate(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            long time = df.parse(date).getTime();
            date = String.valueOf(DateUtils.getRelativeDateTimeString(getApplicationContext(), time,
                    DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_NO_NOON));
            return date;
        } catch (ParseException e) {
            return date;
        }
    }

    private Cursor fetchAllSubmittedWords() {
        return db.query(WordEntry.TABLE_NAME, null, null, null, null, null, WordEntry.COLUMN_NAME_DATETIME + " DESC");
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