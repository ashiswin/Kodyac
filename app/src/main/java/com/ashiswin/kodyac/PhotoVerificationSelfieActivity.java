package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PhotoVerificationSelfieActivity extends AppCompatActivity {
    private static final int INTENT_CAMERA = 0;
    private static final String TAG="PhotoVerificationSelfie";
    TextView txtCode;
    Button btnTakePhoto;
    MainApplication m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_verification_selfie);

        getSupportActionBar().setTitle("Photo Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        m = (MainApplication) getApplicationContext();

        txtCode = (TextView) findViewById(R.id.txtCode);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, INTENT_CAMERA);
                }
            }
        });
    }

    @Override
    //take a picture
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //store the selfie taken so you can bring it up to Face API
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Kodyac/Selfie");
            if(!directory.exists()) {
                directory.mkdirs();
            }
            //get date to name the picture file
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
            java.util.Date now = new java.util.Date();
            //save the picture under correct directory and date
            File file = new File(directory.getAbsolutePath()+"/"+formatter.format(now)+".png");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                //store file path of selfie in Main Application
                m.photoTaken = file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }


            final ProgressDialog dialog = new ProgressDialog(PhotoVerificationSelfieActivity.this);
            dialog.setIndeterminate(true);
            dialog.setTitle("Verifying Photo");
            dialog.setMessage("Please wait while we verify your photo");
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // TODO: Perform photo comparison
                        
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                setResult(RESULT_OK, getIntent());
                                finish();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
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

    private String getStringImage(String absoluteFilePath) {
        Bitmap bmp = BitmapFactory.decodeFile(absoluteFilePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

}
