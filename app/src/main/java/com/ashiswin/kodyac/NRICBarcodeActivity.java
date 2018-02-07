package com.ashiswin.kodyac;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class NRICBarcodeActivity extends AppCompatActivity {

    private Button startScan;
    private TextView barcodeNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nricbarcode);

        getSupportActionBar().setTitle("Scan NRIC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startScan = (Button) findViewById(R.id.scan_bttn);
        barcodeNum = (TextView) findViewById(R.id.barcode_num);

        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(NRICBarcodeActivity.this);
                //initiate scan
                scanIntegrator.setPrompt("Scan barcode at the back of your NRIC");
                scanIntegrator.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                scanIntegrator.initiateScan();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        //once there is result output the barcode number
        if (scanResult!=null){
            String scanContent = scanResult.getContents();
            barcodeNum.setText("NUMBER SCANNED: "+scanContent);
        }else{
            Toast.makeText(this, "No scan result", Toast.LENGTH_SHORT).show();
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
