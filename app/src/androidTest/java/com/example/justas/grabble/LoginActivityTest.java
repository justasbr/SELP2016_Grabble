package com.example.justas.grabble;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    @After
    public void clearApplicationSettings() {
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(mActivityRule.getActivity().getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }

    @Test
    public void generateRandomUsername() {
        // Type text and then press the button.
        onView(withId(R.id.email))
                .check(matches(withText("")));

        onView(withId(R.id.generate_random_name_button)).perform(click());

        onView(withId(R.id.email))
                .check(matches(not(withText(""))));
    }

    @Test
    public void createNewUser() {
        onView(withId(R.id.generate_random_name_button)).perform(click());
        onView(withId(R.id.email_sign_in_button)).perform(click());

        SystemClock.sleep(500);
        allowPermissionsIfNeeded();

        onView(withText(R.string.okay)).perform(click());

        onView(withId(R.id.map))
                .check(matches(isDisplayed()));
    }

    private static void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("ALLOW"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                }
            }
        }
    }
}
