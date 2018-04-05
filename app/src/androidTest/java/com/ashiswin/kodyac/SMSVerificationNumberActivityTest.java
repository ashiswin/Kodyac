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
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
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

        String testNumber = "12345678910456";

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText(testNumber));
        closeSoftKeyboard();

        onView(withId(R.id.edtPhoneNumber))
                .check(matches(withText(testNumber.substring(0,8))));
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

    @Test
    public void IncorrectNumberErrorAlert() {

        String incorrectNumber = "23456789";

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText(incorrectNumber));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        //wait for alertDialog to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Erroneous Input"))
                .check(matches(isDisplayed()));

        onView(withText("Phone number is invalid"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void IncorrectNumberErrorAlertDismiss() {

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText("12364759"));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        //wait for alertDialog to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //dismiss alertDialog
        //alertDialog button is not truly displayed
        onView(withText("OK"))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check that alert dialog has disappeared
        onView(withText("Erroneous Input"))
                .check(doesNotExist());


        onView(withText("Phone number is invalid"))
                .check(doesNotExist());

    }

    @Test
    public void LandlineNumberEnteredAlert() {

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText("61234567"));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        //wait for alertDialog to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Erroneous Input"))
                .check(matches(isDisplayed()));

        onView(withText("Please enter a MOBILE number"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void LandlineNumberEnteredAlertDismiss() {

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        onView(withId(R.id.edtPhoneNumber))
                .perform(click())
                .perform(typeText("62135476"));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .perform(click());

        //wait for alertDialog to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //dismiss alertDialog
        onView(withText("OK"))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Erroneous Input"))
                .check(doesNotExist());

        onView(withText("Please enter a MOBILE number"))
                .check(doesNotExist());


    }
}
