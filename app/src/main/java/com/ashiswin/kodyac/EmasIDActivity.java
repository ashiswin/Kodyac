package com.ashiswin.kodyac;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.microblink.recognizers.blinkid.singapore.combined.SingaporeIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.singapore.combined.SingaporeIDCombinedRecognizerSettings;
import com.microblink.activity.VerificationFlowActivity;
import com.microblink.results.date.Date;
import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;




//Tutorial: https://github.com/BlinkID/blinkid-android#quickDemo
//reference: https://github.com/BlinkID/blinkid-android/blob/master/BlinkIDSample/BlinkIDSampleCustomUI/src/main/java/com/microblink/blinkid/demo/customui/MyScanActivity.java
// Javadoc:https://blinkid.github.io/blinkid-android/com/microblink/recognizers/blinkid/singapore/front/SingaporeIDFrontRecognizerSettings.html
public class EmasIDActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 0x101;
    private Button startBtn;
    private TextView nameText;
    private TextView cardText;
    private TextView raceText;
    private TextView sexText;
    private TextView countryText;
    private TextView dobText;
    private TextView addressText;
    private TextView issueDateText;
    private ImageView profilePic;
    private boolean profilePictest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emas_id);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nameText  = (TextView) findViewById(R.id.txtName);
        cardText = (TextView) findViewById(R.id.txtCardNumber);
        raceText = (TextView) findViewById(R.id.txtRace);
        sexText = (TextView) findViewById(R.id.txtSex);
        countryText = (TextView) findViewById(R.id.txtCountryBirth);
        dobText = (TextView) findViewById(R.id.txtDOB);
        addressText = (TextView) findViewById(R.id.txtAddress);
        issueDateText = (TextView) findViewById(R.id.txtDateOfIssue);
        profilePic = (ImageView) findViewById(R.id.NRICpic);




        RecognizerCompatibilityStatus supportStatus = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
        if (supportStatus != RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
            Toast.makeText(this, "BlinkID is not supported! Reason: " + supportStatus.name(), Toast.LENGTH_LONG).show();
        }


        startBtn = (Button) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingaporeIDCombinedRecognizerSettings settings = new SingaporeIDCombinedRecognizerSettings();
                settings.setEncodeFaceImage(true);
                profilePictest=settings.shouldEncodeFaceImage();
                Intent intent = new Intent(EmasIDActivity.this, VerificationFlowActivity.class);
                intent.putExtra(VerificationFlowActivity.EXTRAS_LICENSE_KEY, getString(R.string.microblink_license_key));
                intent.putExtra(VerificationFlowActivity.EXTRAS_COMBINED_RECOGNIZER_SETTINGS, settings);
                intent.putExtra(VerificationFlowActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
                //TODO: sort out the hints so the UI is better
                intent.putExtra(VerificationFlowActivity.EXTRAS_INSTRUCTIONS_DOCUMENT_FIRST_SIDE, R.string.emas_scan_first_side);
                intent.putExtra(VerificationFlowActivity.EXTRAS_INSTRUCTIONS_DOCUMENT_SECOND_SIDE, R.string.emas_scan_second_side);

                //TODO: include a res ID if possible so users know which side front and back are


                intent.putExtra(VerificationFlowActivity.EXTRAS_SHOW_TIME_LIMITED_LICENSE_KEY_WARNING, false);
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {

                Bundle extras = data.getExtras();
                SingaporeIDCombinedRecognitionResult result = (SingaporeIDCombinedRecognitionResult) extras.getParcelable(VerificationFlowActivity.EXTRAS_COMBINED_RECOGNITION_RESULT);

                if(result.isValid() && !result.isEmpty()) {
                    if (!result.isDocumentDataMatch()) {
                        Toast.makeText(this, "Front and back side are not from the same ID card", Toast.LENGTH_SHORT).show();
                        // front and back sides are not from the same ID card
                    } else {
                        String name = result.getName();
                        String cardNumber = result.getCardNumber();
                        String country = result.getCountryOfBirth();
                        String race = result.getRace();
                        String sex = result.getSex();
                        Date dob = result.getDateOfBirth();
                        String address = result.getAddress();
                        Date issueDate = result.getDocumentDateOfIssue();
                        //TODO: use meta data listener
                        byte[] face = result.getEncodedFaceImage();

                        nameText.setText("name is "+name);
                        cardText.setText("NRIC is "+cardNumber);
                        countryText.setText("Country of birth is "+country);
                        raceText.setText("race is "+race);
                        sexText.setText("Sex is "+sex);
                        dobText.setText("DOB is "+dob.getDay()+"-"+dob.getMonth()+"-"+dob.getYear());
                        addressText.setText("Address is "+address);
                        issueDateText.setText("Date of issue is "+issueDate.getDay()+"-"+issueDate.getMonth()+"-"+issueDate.getYear());

                        if(face!=null){
                            Bitmap bmp = BitmapFactory.decodeByteArray(face,0,face.length);
                            profilePic.setImageBitmap(bmp);
                            //TODO: check performance if not use picasso

                        }else{
                            Toast.makeText(this, "profile pic not detected. Encoding profile pic enabled:"+profilePictest, Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                    // not all relevant data was scanned, ask user
                    // to try again
                }
            }
        }
    }

}
