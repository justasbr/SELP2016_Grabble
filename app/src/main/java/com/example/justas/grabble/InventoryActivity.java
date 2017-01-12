package com.example.justas.grabble;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity implements HistoryStatsFragment.OnFragmentInteractionListener,
        CurrentInventoryFragment.OnFragmentInteractionListener {
    private boolean mShowingInventory;
    private FragmentManager mFragmentManager;
    private SharedPreferences sharedPrefs;

    private Dictionary dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefs = getSharedPreferences(
                getString(R.string.inventory_file), Context.MODE_PRIVATE);

        mFragmentManager = getFragmentManager();

        dictionary = Dictionary.getInstance(getApplicationContext());

        setContentView(R.layout.activity_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        initFragments();
        updateInventory();

        final Button inventory_fragment_button = (Button) findViewById(R.id.inventory_fragment_button);

        inventory_fragment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                Fragment statsFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_stats);
                Fragment inventoryFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_current_inventory);

                if (mShowingInventory) {
                    hideFragment(inventoryFragment);
                    showFragment(statsFragment);

                    mShowingInventory = false;
                    inventory_fragment_button.setText("INVENTORY");
                } else {
                    hideFragment(statsFragment);
                    showFragment(inventoryFragment);

                    mShowingInventory = true;
                    inventory_fragment_button.setText("STATS & HISTORY");
                }

                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        final FloatingActionButton wordSubmitButton =
                (FloatingActionButton) findViewById(R.id.submit_word_button);

        wordSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText wordField = (EditText) findViewById(R.id.submit_word_text);
                String submission = wordField.getText().toString();

                if (isValidWord(submission)) {
                    submitWord(submission);
                    wordField.setText("");
                }
            }
        });
    }


    private boolean isValidWord(String word) {
        if (word.length() != 7) {
            showShortToast(getString(R.string.warning_submitted_word_length));
            return false;
        } else if (!dictionary.containsWord(word)) {
            showShortToast(getString(R.string.warning_submitted_word_not_in_dict));
            return false;
        } else if (!hasLettersFor(word)) {
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

    private boolean hasLettersFor(String word) {
        Map<Character, Integer> charCount = charOccurences(word);
        for (char c : charCount.keySet()) {
            int possessed = sharedPrefs.getInt(String.valueOf(c), 0);
            int needForWord = charCount.get(c);

            if (possessed < needForWord) {
                return false;
            }
        }
        return true;
    }

    private Map<Character, Integer> charOccurences(String word) {
        Map<Character, Integer> occCount = new HashMap<>();
        for (char c : word.toCharArray()) {
            int count = occCount.containsKey(c) ? occCount.get(c) : 0;
            int updatedCount = count + 1;
            occCount.put(c, updatedCount);
        }
        return occCount;
    }

    private void submitWord(String word) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Map<Character, Integer> charCount = charOccurences(word);
        for (char c : charCount.keySet()) {

            String letterLabel = String.valueOf(c);
            int possessed = sharedPrefs.getInt(letterLabel, 0);
            int used = charCount.get(c);

            int updatedCount = possessed - used;

            editor.putInt(letterLabel, updatedCount);
        }

        showLongToast(getString(R.string.word_submitted_congrats) + word);

        editor.commit();
        updateInventory();
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

    private void initFragments() {
        Fragment inventoryFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_stats);
        Fragment statsFragment = mFragmentManager.findFragmentById(R.id.inventory_fragment_stats);

        showFragment(inventoryFragment);
        hideFragment(statsFragment);

        mShowingInventory = true;
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    private void hideFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.hide(fragment);
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onBackPressed() {
        Log.d("BACK CLICKED", "");
        finish();
    }
}
