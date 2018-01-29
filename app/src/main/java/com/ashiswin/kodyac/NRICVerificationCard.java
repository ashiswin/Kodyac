package com.ashiswin.kodyac;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class NRICVerificationCard extends AppCompatActivity {
    private static final int INTENT_SELFIE = 0;
    private static final int INTENT_FRONT = 1;
    private static final int INTENT_BACK = 2;

    ImageButton btnFront, btnBack;
    Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nricverification_card);

        btnFront = (ImageButton) findViewById(R.id.btnFront);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
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
        }
        else if(requestCode == INTENT_BACK && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            btnBack.setImageBitmap(imageBitmap);
        }
    }
}
