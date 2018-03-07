package com.ashiswin.kodyac;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.microblink.recognizers.blinkid.singapore.front.SingaporeIDFrontRecognitionResult;
import com.microblink.activity.ScanActivity;
import com.microblink.activity.ScanCard;
import com.microblink.activity.SegmentScanActivity;
import com.microblink.activity.ShowOcrResultMode;
import com.microblink.activity.VerificationFlowActivity;
import com.microblink.recognizers.BaseRecognitionResult;
import com.microblink.recognizers.RecognitionResults;
import com.microblink.recognizers.blinkid.singapore.back.SingaporeIDBackRecognizerSettings;
import com.microblink.recognizers.blinkid.singapore.combined.SingaporeIDCombinedRecognizerSettings;
import com.microblink.recognizers.blinkid.singapore.front.SingaporeIDFrontRecognizerSettings;
import com.microblink.recognizers.settings.RecognitionSettings;
import com.microblink.recognizers.settings.RecognizerSettings;
import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;
import com.microblink.view.recognition.RecognitionType;


//Tutorial: https://github.com/BlinkID/blinkid-android#quickDemo
//reference: https://github.com/BlinkID/blinkid-android/blob/master/BlinkIDSample/BlinkIDSampleCustomUI/src/main/java/com/microblink/blinkid/demo/customui/MyScanActivity.java
// Javadoc:https://blinkid.github.io/blinkid-android/com/microblink/recognizers/blinkid/singapore/front/SingaporeIDFrontRecognizerSettings.html
public class EmasIDActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 0x101;
    private Button startBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emas_id);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);





        RecognizerCompatibilityStatus supportStatus = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
        if (supportStatus != RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
            Toast.makeText(this, "BlinkID is not supported! Reason: " + supportStatus.name(), Toast.LENGTH_LONG).show();
        }


        startBtn = (Button) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecognitionSettings settings = new RecognitionSettings();
                SingaporeIDFrontRecognizerSettings singaporeFront = new SingaporeIDFrontRecognizerSettings();
                SingaporeIDBackRecognizerSettings singaporeBack = new SingaporeIDBackRecognizerSettings();
                RecognizerSettings[] settArray = new RecognizerSettings[]{singaporeFront, singaporeBack};
                settings.setRecognizerSettingsArray(settArray);
                Intent intent = new Intent(EmasIDActivity.this, ScanCard.class);
                intent.putExtra(ScanCard.EXTRAS_LICENSE_KEY, getString(R.string.microblink_license_key));
                intent.putExtra(ScanCard.EXTRAS_RECOGNITION_SETTINGS,settings);
                intent.putExtra(ScanCard.EXTRAS_BEEP_RESOURCE, R.raw.beep);
                intent.putExtra(ScanActivity.EXTRAS_SHOW_FOCUS_RECTANGLE, true);
                intent.putExtra(SegmentScanActivity.EXTRAS_SHOW_OCR_RESULT_MODE, (Parcelable) ShowOcrResultMode.ANIMATED_DOTS);
                startActivityForResult(intent, MY_REQUEST_CODE);

            }
        });
    }

    public void onScanningDone(RecognitionResults results) {
        BaseRecognitionResult[] dataArray = results.getRecognitionResults();
        for(BaseRecognitionResult baseResult : dataArray) {
            if(baseResult instanceof SingaporeIDFrontRecognitionResult) {
                SingaporeIDFrontRecognitionResult result = (SingaporeIDFrontRecognitionResult) baseResult;

                // you can use getters of SingaporeIDFrontRecognitionResult class to
                // obtain scanned information
                if(result.isValid() && !result.isEmpty()) {
                    String name = result.getName();
                    Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
                    String cardNumber = result.getCardNumber();
                    Toast.makeText(this, cardNumber, Toast.LENGTH_SHORT).show();

                } else {
                    // not all relevant data was scanned, ask user
                    // to try again
                }
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // onActivityResult is called whenever we are returned from activity started
        // with startActivityForResult. We need to check request code to determine
        // that we have really returned from BlinkID activity.
        if (requestCode == MY_REQUEST_CODE) {

            // make sure BlinkID activity returned result
            if (resultCode == Activity.RESULT_OK && data != null) {

                Bundle extras = data.getExtras();
                if (extras != null && extras.getParcelable(ScanActivity.EXTRAS_RECOGNITION_RESULTS) == null) {
                    // VerificationFlowActivity does not return results as RecognitionResults object, prepare RecognitionResults
                    // from combined recognizer result
                    BaseRecognitionResult combinedResult = extras.getParcelable(VerificationFlowActivity.EXTRAS_COMBINED_RECOGNITION_RESULT);
                    if (combinedResult != null) {
                        data.putExtra(ScanActivity.EXTRAS_RECOGNITION_RESULTS, new RecognitionResults(new BaseRecognitionResult[]{combinedResult}, RecognitionType.SUCCESSFUL));
                    }
                }

                // set intent's component to ResultActivity and pass its contents
                // to ResultActivity. ResultActivity will show how to extract
                // data from result.

                Toast.makeText(this, "success here1", Toast.LENGTH_SHORT).show();
                //startActivity(data);
            } else {
                // if BlinkID activity did not return result, user has probably
                // pressed Back button and cancelled scanning
                Toast.makeText(this, "Scan cancelled!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
