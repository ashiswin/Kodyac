package com.ashiswin.kodyac;

import android.content.Intent;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

/**
 * Created by Jing Yun on 5/4/2018.
 */

//overall UI Test is combines the test of all individual methods
//It clicks all methods in the Verification Methods activity
//then it checks if the textView and EditText are displaying the correct things
public class OveralUITest {
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

    @Test
    public void TestAllUI() {
        //let the app load
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //checks that there is a being button
        //and that yuo can clikc it
        onView(withId(R.id.btnBegin))
                .check(matches(isDisplayed()))
                .perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /********************************CHECK VERIFICATION METHODS PAGE*************************/
        //check that all items in the methods are present
        onData(hasToString("sms"))
                .inAdapterView(withId(R.id.lstMethods))
                .check(matches(isDisplayed()));

        onData(hasToString("myinfo"))
                .inAdapterView(withId(R.id.lstMethods))
                .check(matches(isDisplayed()));

        onData(hasToString("nric"))
                .inAdapterView(withId(R.id.lstMethods))
                .check(matches(isDisplayed()));

        onData(hasToString("video"))
                .inAdapterView(withId(R.id.lstMethods))
                .check(matches(isDisplayed()));

        /**********************ENTER SMS ACTIVITY AND CHECK IT*******************************/
        //check that all items in the methods are present
        onData(hasToString("sms"))
                .inAdapterView(withId(R.id.lstMethods))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.sms_instruction))
                .check(matches(isDisplayed()))
                .check(matches(withText("Please enter your phone number")));

        onView(withId(R.id.edtPhoneNumber))
                .check(matches(isDisplayed()))
                .check(matches(withText("")));

        onView(withId(R.id.btnSendSMS))
                .check(matches(isDisplayed()))
                .check(matches(not(isEnabled())));

        pressBack();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*********************88**CHECKS BASIC INFO VERIFICATION***************************888******/
        onData(hasToString("myinfo"))
                .inAdapterView(withId(R.id.lstMethods))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //checks that instruction is correct and error message is absent
        onView(withId(R.id.txtErrorMsg))
                .check(matches(isDisplayed()))
                .check(matches(withText(" ")));

        onView(withId(R.id.myinfo_instruction))
                .check(matches(isDisplayed()))
                .check(matches(withText("Please click to scan NRIC barcode to generate the following information")));
        //checks if button is working
        onView(withId(R.id.scan_bttn))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()));

        //checks that information is default
        onView(withId(R.id.txtName))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Name)")));

        onView(withId(R.id.barcode_num))
                .check(matches(isDisplayed()))
                .check(matches(withText("(IC Number)")));

        onView(withId(R.id.txtRace))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Race)")));

        onView(withId(R.id.txtSex))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Gender)")));

        onView(withId(R.id.txtDOB))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Date of Birth)")));

        onView(withId(R.id.txtCountryBirth))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Nationality)")));

        onView(withId(R.id.txtAddress))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Address)")));

        onView(withId(R.id.scan_bttn))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .check(matches( isEnabled()));

        onView(withId(R.id.btnConfirm))
                .check(matches(isDisplayed()))
                .check(matches( not(isEnabled())));

        pressBack();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /***************CHECKS PHOTO NRIC VERIFICATION********************/

        onData(hasToString("nric"))
                .inAdapterView(withId(R.id.lstMethods))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

         onView(withId(R.id.nric_photo_instructions))
         .check(matches(isDisplayed()))
         .check(matches(withText("Please click to add photo ID and \nperform photo verification")));

         //checks that information is default
         onView(withId(R.id.txtName))
         .check(matches(isDisplayed()))
         .check(matches(withText("(Name)")));

         //checks that information is default
         onView(withId(R.id.txtCardNumber))
         .check(matches(isDisplayed()))
         .check(matches(withText("(IC Number)")));

         onView(withId(R.id.txtRace))
         .check(matches(isDisplayed()))
         .check(matches(withText("(Race)")));

         onView(withId(R.id.txtSex))
         .check(matches(isDisplayed()))
         .check(matches(withText("(Gender)")));

         onView(withId(R.id.txtDOB))
         .check(matches(isDisplayed()))
         .check(matches(withText("(Date of Birth)")));

         onView(withId(R.id.txtCountryBirth))
         .check(matches(isDisplayed()))
         .check(matches(withText("(Nationality)")));

         onView(withId(R.id.txtAddress))
         .check(matches(isDisplayed()))
         .check(matches(withText("(Address)")));

         onView(withId(R.id.startBtn))
         .check(matches(isDisplayed()))
         .check(matches(isEnabled()))
         .check(matches(isClickable()));

         onView(withId(R.id.btnConfirm))
         .check(matches(isDisplayed()))
         .check(matches(not(isEnabled())));

        onView(withId(R.id.btnPhotoVerification))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .check(matches(isClickable()))
                .perform(click());

        onView(withId(R.id.btnTakePhoto))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .check(matches(isClickable()));

        pressBack();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pressBack();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /***********************8**CHECKS VIDEO NRIC VERIFICATION********************88******************/
        onData(hasToString("video"))
                .inAdapterView(withId(R.id.lstMethods))
                .perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //checks that information is default
        onView(withId(R.id.nric_video_instructions))
                .check(matches(isDisplayed()))
                .check(matches(withText("Please click to add photo ID and \nperform video verification")));

        //checks that information is default
        onView(withId(R.id.txtName))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Name)")));

        //checks that information is default
        onView(withId(R.id.txtCardNumber))
                .check(matches(isDisplayed()))
                .check(matches(withText("(IC Number)")));

        onView(withId(R.id.txtRace))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Race)")));

        onView(withId(R.id.txtSex))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Gender)")));

        onView(withId(R.id.txtDOB))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Date of Birth)")));

        onView(withId(R.id.txtCountryBirth))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Nationality)")));

        onView(withId(R.id.txtAddress))
                .check(matches(isDisplayed()))
                .check(matches(withText("(Address)")));

        onView(withId(R.id.startBtn))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .check(matches(isClickable()));

        onView(withId(R.id.btnVideoVerification))
                .check(matches(isDisplayed()))
                .check(matches(isEnabled()))
                .check(matches(isClickable()));

        onView(withId(R.id.btnConfirm))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .check(matches(not(isEnabled())));

    }
}
