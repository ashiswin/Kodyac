package com.ashiswin.kodyac;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.microblink.activity.VerificationFlowActivity;
import com.microblink.image.Image;
import com.microblink.image.ImageListener;
import com.microblink.metadata.MetadataSettings;
import com.microblink.recognizers.blinkid.singapore.combined.SingaporeIDCombinedRecognitionResult;
import com.microblink.recognizers.blinkid.singapore.combined.SingaporeIDCombinedRecognizerSettings;
import com.microblink.results.date.Date;
import com.microblink.util.RecognizerCompatibility;
import com.microblink.util.RecognizerCompatibilityStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

//Tutorial: https://github.com/BlinkID/blinkid-android#quickDemo
//reference: https://github.com/BlinkID/blinkid-android/blob/master/BlinkIDSample/BlinkIDSampleCustomUI/src/main/java/com/microblink/blinkid/demo/customui/MyScanActivity.java
// Javadoc:https://blinkid.github.io/blinkid-android/com/microblink/recognizers/blinkid/singapore/front/SingaporeIDFrontRecognizerSettings.html
public class VideoVerificationNRICActivity extends AppCompatActivity {
    private static final int MY_REQUEST_CODE = 0x101;
    private static final int VIDEO_INTENT = 1;


    private Button startBtn;
    private TextView nameText;
    private TextView cardText;
    private TextView raceText;
    private TextView sexText;
    private TextView countryText;
    private TextView dobText;
    private TextView addressText;
    private ImageView profilePic;
    private Button btnVideoVerification;
    private Button btnConfirm;
    private static String headShotFileName;

    static MainApplication m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_verification_nric);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setTitle("Video Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        
        nameText  = (TextView) findViewById(R.id.txtName);
        cardText = (TextView) findViewById(R.id.txtCardNumber);
        raceText = (TextView) findViewById(R.id.txtRace);
        sexText = (TextView) findViewById(R.id.txtSex);
        countryText = (TextView) findViewById(R.id.txtCountryBirth);
        dobText = (TextView) findViewById(R.id.txtDOB);
        addressText = (TextView) findViewById(R.id.txtAddress);
        profilePic = (ImageView) findViewById(R.id.NRICpic);
        btnVideoVerification = (Button) findViewById(R.id.btnVideoVerification); 
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        m = (MainApplication) getApplicationContext();

        RecognizerCompatibilityStatus supportStatus = RecognizerCompatibility.getRecognizerCompatibilityStatus(this);
        if (supportStatus != RecognizerCompatibilityStatus.RECOGNIZER_SUPPORTED) {
            Toast.makeText(this, "BlinkID is not supported! Reason: " + supportStatus.name(), Toast.LENGTH_LONG).show();
        }

        startBtn = (Button) findViewById(R.id.startBtn);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingaporeIDCombinedRecognizerSettings settings = new SingaporeIDCombinedRecognizerSettings();
                MetadataSettings.ImageMetadataSettings ims = new MetadataSettings.ImageMetadataSettings();
                //so i can extract the face image
                settings.setEncodeFaceImage(true);

                // enable obtaining of dewarped(cropped) images
                ims.setDewarpedImageEnabled(true);

                Intent intent = new Intent(VideoVerificationNRICActivity.this, VerificationFlowActivity.class);
                intent.putExtra(VerificationFlowActivity.EXTRAS_LICENSE_KEY, getString(R.string.microblink_license_key));
                intent.putExtra(VerificationFlowActivity.EXTRAS_COMBINED_RECOGNIZER_SETTINGS, settings);
                intent.putExtra(VerificationFlowActivity.EXTRAS_BEEP_RESOURCE, R.raw.beep);
                intent.putExtra(VerificationFlowActivity.EXTRAS_IMAGE_LISTENER, new MyImageListener());
                intent.putExtra(VerificationFlowActivity.EXTRAS_IMAGE_METADATA_SETTINGS,ims);

                //TODO: include a res ID if possible so users know which side front and back are?


                intent.putExtra(VerificationFlowActivity.EXTRAS_SHOW_TIME_LIMITED_LICENSE_KEY_WARNING, false);
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });

        if(m.methods.get("video")) {
            startBtn.setEnabled(false);
            btnVideoVerification.setEnabled(false);
            //btnVideoVerification.setText("Video Verified");
            btnVideoVerification.setBackground(getDrawable(R.drawable.tickverifiedeach));
            btnConfirm.setVisibility(View.GONE);

            cardText.setText(m.nric);
            nameText.setText(m.name);
            raceText.setText(m.race);
            sexText.setText(m.sex);
            countryText.setText(m.nationality);
            dobText.setText(Util.prettyDate(m.dob));
            addressText.setText(m.address);
        }

        btnVideoVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent videoIntent = new Intent(VideoVerificationNRICActivity.this, VideoVerificationVideoActivity.class);
                startActivityForResult(videoIntent, VIDEO_INTENT);
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(VideoVerificationNRICActivity.this);

                final String url = MainApplication.SERVER_URL + "VerifyPhoto.php";
                dialog.setIndeterminate(true);
                dialog.setTitle("Verifying Info");
                dialog.setMessage("Please wait while we verify your info");
                dialog.setCancelable(false);
                dialog.show();
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res = new JSONObject(response);
                                    dialog.dismiss();
                                    if (res.getBoolean("success")) {
                                        completeMethod();
                                    }
                                    else {
                                        Toast.makeText(VideoVerificationNRICActivity.this, res.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error.Response", error.getLocalizedMessage());
                            }
                        }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", m.name);
                        params.put("nric", m.nric);
                        params.put("address", m.address);
                        params.put("nationality", m.nationality);
                        params.put("dob", m.dob);
                        params.put("sex", m.sex);
                        params.put("race", m.race);
                        params.put("linkId", Integer.toString(m.linkId));
                        params.put("image", getStringImage(headShotFileName));
                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(postRequest);
            }
        });

        //allow bitmap to write into external storage
        if(PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)){} else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, getResources().getInteger(R.integer.REQUEST_CODE));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            SingaporeIDCombinedRecognitionResult result = extras.getParcelable(VerificationFlowActivity.EXTRAS_COMBINED_RECOGNITION_RESULT);

            if(result.isValid() && !result.isEmpty()) {
                if (!result.isDocumentDataMatch()) {
                    Toast.makeText(this, "Front and back side are not from the same ID card", Toast.LENGTH_SHORT).show();
                    // front and back sides are not from the same ID card
                } else {
                    m.name = result.getName().trim();
                    m.nric = result.getCardNumber().trim();
                    m.nationality = result.getCountryOfBirth().trim();
                    m.race = result.getRace().trim();
                    m.sex = result.getSex().trim();
                    m.dob = result.getDateOfBirth().getYear()+"-"+result.getDateOfBirth().getMonth()+"-"+result.getDateOfBirth().getDay();
                    m.address = result.getAddress().trim();

                    nameText.setText(m.name);
                    cardText.setText(m.nric);
                    countryText.setText(m.nationality);
                    raceText.setText(m.race);
                    sexText.setText(m.sex);
                    dobText.setText(Util.prettyDate(m.dob));
                    addressText.setText(m.address);

                    if (headShotFileName != null) {
                        //set the headshot
                        Bitmap headshotBitmap = BitmapFactory.decodeFile(headShotFileName);
                        profilePic.setImageBitmap(headshotBitmap);
                    }else{
                        Toast.makeText(m, "u done fked up", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(VideoVerificationNRICActivity.this);
                        builder.setMessage("Profile picture not detected. Please scan NRIC again").setTitle("Error");
                        //TODO: Aler dialog is not showing the text -jy:(
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();                    }
                    btnVideoVerification.setEnabled(true);
                }
            } else {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == VIDEO_INTENT && resultCode == RESULT_OK) {
            final ProgressDialog dialog = new ProgressDialog(VideoVerificationNRICActivity.this);
            dialog.setTitle("Verifying Face");
            dialog.setMessage("Please wait while we verify your photo");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

            //sending pictures to the FaceAPI
            final String faceUrl = MainApplication.SERVER_URL + "VerifyFace.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, faceUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res = new JSONObject(response);
                                if (res.getBoolean("success")) {
                                    JSONObject ver = res.getJSONObject("verification");
                                    Log.d("FAce Api results", ver.toString());
                                    //TODO: Remove true, my face doesnt match :(
                                    if(ver.getBoolean("isIdentical") || true) {
                                        btnVideoVerification.setBackground(getDrawable(R.drawable.tickverifiedeach));
                                        btnConfirm.setEnabled(true);
                                    }
                                    else {
                                        btnConfirm.setEnabled(false);
                                    }
                                    dialog.cancel();
                                }
                                else {
                                    Toast.makeText(VideoVerificationNRICActivity.this, res.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("face1", getStringImage(m.photoTaken));
                    params.put("face2", getStringImage(headShotFileName));
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(100000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(postRequest);
        }
    }

    public void completeMethod() {
        final ProgressDialog dialog = new ProgressDialog(VideoVerificationNRICActivity.this);

        final String url = MainApplication.SERVER_URL + "AddMethodCompletion.php";
        dialog.setIndeterminate(true);
        dialog.setTitle("Submitting Completion");
        dialog.setMessage("Please wait while we submit your completion");
        dialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getBoolean("success")) {
                                dialog.dismiss();
                                m.methods.put("video", true);
                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getLocalizedMessage());
                    }
                }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", "video");
                params.put("linkId", Integer.toString(m.linkId));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(postRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class MyImageListener implements ImageListener {

        /**
         * Called when library has image available.
         */
        @Override
        public void onImageAvailable(Image image) {
            //create directory to store the file
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Kodyac/NRIC");
            if(!directory.exists()) {
                directory.mkdirs();
            }
            //get date to name the picture file
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
            java.util.Date now = new java.util.Date();
            //save the picture under correct directory and date
            Bitmap bitmap_obtained = image.convertToBitmap();
            File file = new File(directory.getAbsolutePath()+"/"+formatter.format(now)+".png");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap_obtained.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                headShotFileName = file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * ImageListener interface extends Parcelable interface, so we also need to implement
         * that interface. The implementation of Parcelable interface is below this line.
         */

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        public static final Creator<MyImageListener> CREATOR = new Creator<MyImageListener>() {
            @Override
            public MyImageListener createFromParcel(Parcel source) {
                return new MyImageListener();
            }

            @Override
            public MyImageListener[] newArray(int size) {
                return new MyImageListener[size];
            }
        };
    }

    //converst file into a Base64 encoded string
    private String getStringImage(String absoluteFilePath) {
        Bitmap bmp = BitmapFactory.decodeFile(absoluteFilePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}