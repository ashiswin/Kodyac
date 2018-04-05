package com.ashiswin.kodyac;

import android.content.Intent;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.hasToString;

/**
 * Created by Jing Yun on 6/4/2018.
 */

public class BlinkIDAccuracy {
    /********Fill in your own details****/
    private String NAME = "";
    private String NRIC = "";
    private String RACE = "";
    private String CountryOfBirth = "";
    private String DOB = "";
    private String SEX = "";

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = super.getActivityIntent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.kodyac.tech/links/kyc.php?id=18"));
            return intent;
        }
    };

    @Before
    public void EnterRelevantActivity(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //checks that there is a being button
        //and that yuo can clikc it
        onView(withId(R.id.btnBegin))
                .perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check that all items in the methods are present
        onData(hasToString("nric"))
                .inAdapterView(withId(R.id.lstMethods))
                .perform(click());

        onView(withId(R.id.startBtn))
                .perform(click());

        /**SCAN NRIC***/
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestSex() {
        onView(withId(R.id.txtSex))
                .check(matches(isDisplayed()))
                .check(matches(withText(SEX)));
    }

    @Test
    public void TestName() {
        onView(withId(R.id.txtName))
                .check(matches(isDisplayed()))
                .check(matches(withText(NAME)));
    }

    @Test
    public void TestNRIC() {
        onView(withId(R.id.txtCardNumber))
                .check(matches(isDisplayed()))
                .check(matches(withText(NRIC)));
    }

    @Test
    public void TestRace() {
        onView(withId(R.id.txtRace))
                .check(matches(isDisplayed()))
                .check(matches(withText(RACE)));
    }

    @Test
    public void TestDOB() {
        onView(withId(R.id.txtDOB))
                .check(matches(isDisplayed()))
                .check(matches(withText(DOB)));
    }

    @Test
    public void TestCountry() {
        onView(withId(R.id.txtCountryBirth))
                .check(matches(isDisplayed()))
                .check(matches(withText(CountryOfBirth)));
    }

}
