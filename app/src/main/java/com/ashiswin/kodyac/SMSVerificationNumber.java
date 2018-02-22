package com.ashiswin.kodyac;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

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
                String countryCode = "+65";
                String phoneNumb = edtPhoneNumber.getText().toString().trim();
                if (edtPhoneNumber.getText().toString().length()!=8){
                    Toast.makeText(SMSVerificationNumber.this, "Phone number should be 8 digits long", Toast.LENGTH_SHORT).show();
                }
                else if (Patterns.PHONE.matcher(phoneNumb).matches()){
                    boolean status = validateWithLibPhoneNumb(countryCode, phoneNumb);
                    boolean isMobile = validateMobile(countryCode, phoneNumb);
                    if (status){
                        if (isMobile) {
                            //doesnt check if mobile number is fake
                            Intent otpIntent = new Intent(SMSVerificationNumber.this, SMSVerificationOTP.class);
                            otpIntent.putExtra("methodId", getIntent().getIntExtra("methodId", 0));
                            startActivityForResult(otpIntent, INTENT_OTP);
                        }
                        else{
                            edtPhoneNumber.getText().clear();
                            Toast.makeText(SMSVerificationNumber.this, "Please enter a MOBILE number", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        edtPhoneNumber.getText().clear();
                        Toast.makeText(SMSVerificationNumber.this, "Phone number is invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(SMSVerificationNumber.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.country_codes));
        spnCountryCodes.setAdapter(adapter);
    }

    private boolean validateWithLibPhoneNumb(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            //Toast.makeText(this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean validateMobile(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }
        PhoneNumberUtil.PhoneNumberType isMobile = phoneNumberUtil.getNumberType(phoneNumber);
        return (PhoneNumberUtil.PhoneNumberType.MOBILE==isMobile);
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
