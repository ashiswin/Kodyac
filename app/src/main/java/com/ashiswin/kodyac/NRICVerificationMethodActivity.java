package com.ashiswin.kodyac;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.vision.barcode.Barcode;

public class NRICVerificationMethodActivity extends AppCompatActivity {
    private Button startOCR;
    private Button startBarcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nricverification_method);

        startOCR=(Button)findViewById(R.id.selectOCR) ;
        startBarcode=(Button)findViewById(R.id.selectBarcode) ;

        startOCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NRIC_OCR_Intent = new Intent(NRICVerificationMethodActivity.this, EmasIDActivity.class);
                startActivity(NRIC_OCR_Intent);
            }
        });

        startBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent barcodeIntent = new Intent(NRICVerificationMethodActivity.this, NRICBarcodeActivity.class);
                startActivity(barcodeIntent);

            }
        });




    }
}
