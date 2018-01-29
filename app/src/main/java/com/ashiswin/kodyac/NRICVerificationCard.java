package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class NRICVerificationCard extends AppCompatActivity {
    private static final int INTENT_SELFIE = 0;
    private static final int INTENT_FRONT = 1;
    private static final int INTENT_BACK = 2;

    ImageButton btnFront, btnBack;
    TextView txtName, txtNRIC, txtDOB, txtAddress;
    Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nricverification_card);

        getSupportActionBar().setTitle("Upload NRIC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnFront = (ImageButton) findViewById(R.id.btnFront);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        txtName = (TextView) findViewById(R.id.txtName);
        txtNRIC = (TextView) findViewById(R.id.txtNRIC);
        txtDOB = (TextView) findViewById(R.id.txtDOB);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        btnVerify = (Button) findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selfieIntent = new Intent(NRICVerificationCard.this, NRICVerificationSelfie.class);
                selfieIntent.putExtra("methodId", getIntent().getIntExtra("methodId", 0));
                startActivityForResult(selfieIntent, INTENT_SELFIE);
            }
        });

        btnFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, INTENT_FRONT);
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, INTENT_BACK);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_SELFIE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
        else if(requestCode == INTENT_FRONT && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            btnFront.setImageBitmap(imageBitmap);

            final ProgressDialog dialog = new ProgressDialog(NRICVerificationCard.this);
            dialog.setIndeterminate(true);
            dialog.setTitle("Scanning NRIC");
            dialog.setMessage("Please wait while we scan your NRIC");
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                                txtName.setText("Isaac Ashwin Ravindran");
                                txtNRIC.setText("S9999999Z");
                                txtDOB.setText("4th October 1995");
                                txtAddress.setText("Blk 59 Changi South Avenue, #09-102, S453199");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else if(requestCode == INTENT_BACK && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            btnBack.setImageBitmap(imageBitmap);
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
}
