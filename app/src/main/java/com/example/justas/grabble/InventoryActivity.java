package com.example.justas.grabble;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justas.grabble.data.SubmittedWordsContract.WordEntry;
import com.example.justas.grabble.data.SubmittedWordsOpenHelper;
import com.example.justas.grabble.helper.Callbacks;
import com.example.justas.grabble.helper.ScoredWord;
import com.example.justas.grabble.helper.WordSubmission;

import static com.example.justas.grabble.Utility.getDate;
import static com.example.justas.grabble.Utility.getDateTime;

public class InventoryActivity extends AppCompatActivity {
    private static final String SUGGESTIONS_REPLENISHED_DATE = "suggestion_replenished_on";
    private static final String SUGGESTIONS_LEFT = "suggestions_left";
    private static final int SUGGESTIONS_PER_DAY = 3;

    private SharedPreferences sharedPrefs;

    private EditText mWordField;
    private Button mSuggestWordButton;
    private FloatingActionButton mSubmitWordButton;

    private SubmittedWordsOpenHelper mDbHelper;
    private SQLiteDatabase db;

    private Dictionary dictionary;
    private WordEvaluator wordEvaluator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDbHelper = new SubmittedWordsOpenHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();

        sharedPrefs = getSharedPreferences(
                getString(R.string.inventory_file), Context.MODE_PRIVATE);

        dictionary = Dictionary.getInstance(getApplicationContext());

        wordEvaluator = new WordEvaluator(getApplicationContext());

        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mSuggestWordButton = (Button) findViewById(R.id.suggest_word_button);
        mWordField = (EditText) findViewById(R.id.submit_word_text);

        mWordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        updateSuggestionCount();
        updateInventory();

        mSubmitWordButton =
                (FloatingActionButton) findViewById(R.id.submit_word_button);


        mSubmitWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSubmitWordButton.setEnabled(false);

                String submission = mWordField.getText().toString();
                if (isValidWord(submission)) {
                    submitWord(submission);
                    mWordField.setText("");
                }

                mSubmitWordButton.setEnabled(true);
            }
        });


        mSuggestWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSuggestWordButton.setEnabled(false);
                ScoredWord scoredWord = dictionary.getSuggestion();
                mSuggestWordButton.setEnabled(true);

                if (scoredWord != null) {
                    decrementSuggestionCount();
                    mWordField.setText(scoredWord.word);
                    Toast.makeText(getApplicationContext(), "This will net you " + String.valueOf(scoredWord.score)
                            + " points.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "There are no possible words yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void decrementSuggestionCount() {
        int suggestionsLeft = sharedPrefs.getInt(SUGGESTIONS_LEFT, 0);
        suggestionsLeft--;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(SUGGESTIONS_LEFT, suggestionsLeft);
        editor.commit();

        updateSuggestionButton();
    }

    private void updateSuggestionCount() {
        String today = getDate();
        String lastReplenished = sharedPrefs.getString(SUGGESTIONS_REPLENISHED_DATE, "");

        if (!today.equals(lastReplenished)) {
            SharedPreferences.Editor editor = sharedPrefs.edit();

            editor.putString(SUGGESTIONS_REPLENISHED_DATE, today);
            editor.putInt(SUGGESTIONS_LEFT, SUGGESTIONS_PER_DAY);

            editor.commit();
        }
        updateSuggestionButton();
    }

    private void updateSuggestionButton() {
        int suggestionsLeft = Math.max(0, sharedPrefs.getInt(SUGGESTIONS_LEFT, 0));

        String btnText = getString(R.string.get_suggestion) +
                " (" + String.valueOf(suggestionsLeft) + ")";

        mSuggestWordButton.setEnabled(suggestionsLeft > 0);
        mSuggestWordButton.setText(btnText);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private boolean isValidWord(String word) {
        if (word.length() != 7) {
            showShortToast(getString(R.string.warning_submitted_word_length));
            return false;
        } else if (!dictionary.containsWord(word)) {
            showShortToast(getString(R.string.warning_submitted_word_not_in_dict));
            return false;
        } else if (!wordEvaluator.hasLettersFor(word)) {
            showShortToast(getString(R.string.warning_submitted_word_not_enough_letters));
            return false;
        } else {
            return true;
        }
    }

    private void showShortToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void showLongToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void submitWord(String word) {
        wordEvaluator.decrementLetters(word);
        storeSubmission(word);
        sendSubmissionToServer(word);
        updateInventory();
        showLongToast(getString(R.string.word_submitted_congrats) + word);
    }

    private void sendSubmissionToServer(String word) {
        String id = IdentificationUtils.getAndroidId(getApplicationContext());
        WordSubmission submission = new WordSubmission(word, id);

        //Fire and forget
        ServerService.submitWord(submission, Callbacks.empty());
    }

    private void storeSubmission(String word) {

        WordEvaluator wordEvaluator = new WordEvaluator(getApplicationContext());
        ContentValues values = new ContentValues();
        int score = wordEvaluator.wordScoreOf(word);

        values.put(WordEntry.COLUMN_NAME_WORD, word);
        values.put(WordEntry.COLUMN_NAME_SCORE, score);
        values.put(WordEntry.COLUMN_NAME_DATETIME, getDateTime());

        db.insert(WordEntry.TABLE_NAME, null, values);
        Log.d("WORD SUBMITTED SQLite", word + " " + score);

    }

    private void updateInventory() {
        for (char c = 'A'; c <= 'Z'; c++) {
            String label = String.valueOf(c);
            int count = sharedPrefs.getInt(label, 0);

            String textViewId = "text_inventory_" + label;
            int resourceId = getResources().getIdentifier(textViewId, "id", getPackageName());
            TextView textView = (TextView) findViewById(resourceId);

            if (textView != null) {
                String inventoryText = label + ": " + String.valueOf(count);
                textView.setText(inventoryText);
                setFontStyle(textView, count);
            }
        }
    }

    private void setFontStyle(TextView textView, int count) {
        if (count > 0) {
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setTextColor(Color.BLACK);
        } else {
            textView.setTypeface(textView.getTypeface(), Typeface.NORMAL);
            textView.setTextColor(Color.DKGRAY);
        }
    }

    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}
