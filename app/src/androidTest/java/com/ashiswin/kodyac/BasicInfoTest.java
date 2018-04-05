package com.ashiswin.kodyac;

/**
 * Created by Jing Yun on 6/4/2018.
 */

import android.content.Intent;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

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

/*******This is a test to test how accurate the barcode scanner is at determining your NRIC*******/
public class BasicInfoTest {
    /***********ENTER YOUR NRIC HERE********/
    private String NRIC="";

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
        onData(hasToString("myinfo"))
                .inAdapterView(withId(R.id.lstMethods))
                .perform(click());

        onView(withId(R.id.scan_bttn))
                .perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestBarcodeScan() {

        onView(withId(R.id.barcode_num))
                .check(matches(isDisplayed()))
                .check(matches(withText(NRIC)));


    }

}
