package com.example.justas.grabble;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.justas.grabble.utils.WordEvaluator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class WordEvaluatorServiceTest {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    private Context mContext;
    private WordEvaluator mWordEvaluator;
    private SharedPreferences sharedPrefs;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        mWordEvaluator = new WordEvaluator(mContext);
        sharedPrefs = mContext.getSharedPreferences(
                mContext.getString(R.string.inventory_file), Context.MODE_PRIVATE);
    }

    @Test
    public void calculatesScoresCorrectly() {
        String WORD_1 = "BUZZWIG";
        int EXPECTED_SCORE_1 = 124;

        String WORD_2 = "ALGEBRA";
        int EXPECTED_SCORE_2 = 64;

        assertEquals(mWordEvaluator.wordScoreOf(WORD_1), EXPECTED_SCORE_1);
        assertEquals(mWordEvaluator.wordScoreOf(WORD_2), EXPECTED_SCORE_2);
    }

    @Test
    public void calculatesScoresCorrectly_givenExample() {
        String WORD = "LOOKING";
        int EXPECTED_SCORE = 70;

        assertEquals(mWordEvaluator.wordScoreOf(WORD), EXPECTED_SCORE);
    }

    @Test
    public void caseInsensitive() {
        String word = "aLgeBrA";

        assertEquals(mWordEvaluator.wordScoreOf(word),
                mWordEvaluator.wordScoreOf(word.toLowerCase()));
    }

    @Test
    public void countsLettersCorrectly() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.putInt("A", 33).putInt("B", 22).putInt("C", 11);

        editor.commit();

        assertEquals(mWordEvaluator.numberOfLettersOwned(), 66);
    }

    @Test
    public void checksIfEnoughLettersForWord() {
        String word = "STUDENT";

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("T", 0).commit();

        assertFalse(mWordEvaluator.hasLettersFor(word));

        editor.putInt("S", 10).putInt("T", 10)
                .putInt("U", 10).putInt("D", 20)
                .putInt("E", 10).putInt("N", 10)
                .commit();

        assertTrue(mWordEvaluator.hasLettersFor(word));
    }

    @Test
    public void decrementsLettersCorrectly() {
        String word = "AAAABBB";
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("A", 14).putInt("B", 6).putInt("D", 5).commit();

        mWordEvaluator.decrementLetters(word);

        assertEquals(sharedPrefs.getInt("A", 0), 10);
        assertEquals(sharedPrefs.getInt("B", 0), 3);
        assertEquals(sharedPrefs.getInt("D", 0), 5);
    }


}
