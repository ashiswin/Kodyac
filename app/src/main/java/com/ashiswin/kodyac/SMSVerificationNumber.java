package com.ashiswin.kodyac;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SMSVerificationNumber extends AppCompatActivity {
    private static final int INTENT_OTP = 0;
    Spinner spnCountryCodes;
    EditText edtPhoneNumber;
    Button btnSendSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification_number);
        getSupportActionBar().setTitle("SMS Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spnCountryCodes = (Spinner) findViewById(R.id.spnCountryCode);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: can use google libphone number to validate
                if (edtPhoneNumber.getText().toString().charAt(0)=='9' | edtPhoneNumber.getText().toString().charAt(0)=='8'){
                        Intent otpIntent = new Intent(SMSVerificationNumber.this, SMSVerificationOTP.class);
                        otpIntent.putExtra("methodId", getIntent().getIntExtra("methodId", 0));
                        startActivityForResult(otpIntent, INTENT_OTP);
                }
                else{
                    edtPhoneNumber.getText().clear();
                    Toast.makeText(SMSVerificationNumber.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.country_codes));
        spnCountryCodes.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_OTP && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
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
