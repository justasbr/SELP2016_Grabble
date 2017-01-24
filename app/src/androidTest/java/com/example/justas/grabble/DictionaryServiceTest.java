package com.example.justas.grabble;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.justas.grabble.utils.Dictionary;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class DictionaryServiceTest {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    private Context mContext;
    private Dictionary mDict;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
        mDict = Dictionary.getInstance(mContext);
    }

    @Test
    public void containsValidWord() {
        String VALID_WORD = "Student";

        assertTrue(mDict.containsWord(VALID_WORD));
    }

    @Test
    public void doesNotContainInvalidWord() {
        String INVALID_WORD = "Aaaaaaa";

        assertFalse(mDict.containsWord(INVALID_WORD));
    }

    @Test
    public void handlesNull() {
        assertFalse(mDict.containsWord(null));
    }

}
