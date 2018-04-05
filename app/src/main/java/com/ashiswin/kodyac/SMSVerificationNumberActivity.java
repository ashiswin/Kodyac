package com.ashiswin.kodyac;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class SMSVerificationNumberActivity extends AppCompatActivity {
    private static final int INTENT_OTP = 0;
    private static final String COUNTRY_CODE = "+65";
    private static final String INSUFFICIENT_LENGTH = "Phone number should be 8 digits long";
    private static final String INVALID_NUMB = "Phone number is invalid";
    private static final String LANDLINE_NUMD = "Please enter a MOBILE number";
    private static final String VALID_NUMB="Phone number entered is valid";
    Spinner spnCountryCodes;
    EditText edtPhoneNumber;
    Button btnSendSMS;

    MainApplication m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification_number);
        getSupportActionBar().setTitle("SMS Verification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        m = (MainApplication) getApplicationContext();

        spnCountryCodes = (Spinner) findViewById(R.id.spnCountryCode);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);

        if(m.methods.get("sms")) {
            btnSendSMS.setEnabled(false);
            edtPhoneNumber.setText(m.contact);
            edtPhoneNumber.setEnabled(false);
            spnCountryCodes.setVisibility(View.GONE);
        }

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>7){
                    btnSendSMS.setEnabled(true);
                }else{
                    btnSendSMS.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumb = edtPhoneNumber.getText().toString().trim();
                String result = ValidatePhoneNumb(COUNTRY_CODE,phoneNumb);
                if (result.equals(VALID_NUMB)){
                    final String url = MainApplication.SERVER_URL + "SendSMSOTP.php";
                    final String phone = COUNTRY_CODE + edtPhoneNumber.getText().toString();
                    Log.d("phone no?:", "****"  + "****" + edtPhoneNumber.getText() +"****");

                    SMSSendRunnable Send_SMS = new SMSSendRunnable(getApplicationContext(), url, m.linkId, phone);
                    Thread T = new Thread(Send_SMS);
                    T.start();

                    Intent otpIntent = new Intent(SMSVerificationNumberActivity.this, SMSVerificationOTPActivity.class);
                    otpIntent.putExtra("linkId", getIntent().getIntExtra("linkId", m.linkId));
                    otpIntent.putExtra("phone", phone);
                    startActivityForResult(otpIntent, INTENT_OTP);
                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(SMSVerificationNumberActivity.this,R.style.MyDialogTheme);

                    builder.setTitle("Erroneous Input")
                            .setMessage(result)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                    edtPhoneNumber.getText().clear();
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.country_codes));
        spnCountryCodes.setAdapter(adapter);
    }

    private String ValidatePhoneNumb(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        if (phNumber.length()!=8){
            return SMSVerificationNumberActivity.INSUFFICIENT_LENGTH;
        }
        try {
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
            return SMSVerificationNumberActivity.INVALID_NUMB;
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        boolean isMobile = phoneNumberUtil.getNumberType(phoneNumber)==PhoneNumberUtil.PhoneNumberType.MOBILE;
        if (isValid) {
            if (isMobile){
                return SMSVerificationNumberActivity.VALID_NUMB;
            }else{
                return SMSVerificationNumberActivity.LANDLINE_NUMD;
            }
        } else {
            return SMSVerificationNumberActivity.INVALID_NUMB;
        }
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
