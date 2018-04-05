package com.ashiswin.kodyac;


import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SMSVerificationNumberActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = super.getActivityIntent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.kodyac.tech/links/kyc.php?id=52"));
            return intent;
        }
    };

    @Before
    public void EnterSMSActivity(){
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
        onData(hasToString("sms"))
                .inAdapterView(withId(R.id.lstMethods))
                .perform(click());
    }


    @Test
    public void InsufficientNumbers() {

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText("1234"));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));


    }

    @Test
    public void ButtonEnablesAfterSufficientNumbers() {

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText("12345678"));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));
    }

    @Test
    public void InputLimitedToEightNumbers() {

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText("12345678910456"));
        closeSoftKeyboard();

        onView(withId(R.id.edtPhoneNumber))
                .check(matches(withText("12345678")));
    }

    @Test
    public void TextClearsAfterSubmission() {

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText("12345678"));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        //dismiss alertDialog
        onView(withText("OK"))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        onView(withId(R.id.edtPhoneNumber))
                .check(matches(withText("")));
    }
}
