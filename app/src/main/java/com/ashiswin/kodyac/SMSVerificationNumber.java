package com.ashiswin.kodyac;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SMSVerificationNumber extends AppCompatActivity {
    private static final int INTENT_OTP = 0;

    EditText edtPhoneNumber;
    Button btnSendSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification_number);
        getSupportActionBar().setTitle("SMS Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otpIntent = new Intent(SMSVerificationNumber.this, SMSVerificationOTP.class);
                otpIntent.putExtra("methodId", getIntent().getIntExtra("methodId", 0));
                startActivityForResult(otpIntent, INTENT_OTP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_OTP && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
