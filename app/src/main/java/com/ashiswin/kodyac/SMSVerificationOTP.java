package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SMSVerificationOTP extends AppCompatActivity {
    EditText edtOTP;
    Button btnVerify;
    TextView txtResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification_otp);
        getSupportActionBar().setTitle("Verify OTP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtOTP = (EditText) findViewById(R.id.edtOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        txtResend = (TextView) findViewById(R.id.txtResendSMS);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Check valid
                final ProgressDialog dialog = new ProgressDialog(SMSVerificationOTP.this);
                dialog.setIndeterminate(true);
                dialog.setTitle("Verifying OTP");
                dialog.setMessage("Please wait while we verify your OTP");
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
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SMSVerificationOTP.this, "SMS Sent", Toast.LENGTH_SHORT).show();
            }
        });
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
