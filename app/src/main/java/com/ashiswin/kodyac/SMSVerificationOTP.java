package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SMSVerificationOTP extends AppCompatActivity {
    EditText edtOTP;
    Button btnVerify;
    TextView txtResend;
    TextView VerifiedTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification_otp);
        getSupportActionBar().setTitle("Verify OTP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int Lid = 17, linkId = 17;

        edtOTP = (EditText) findViewById(R.id.edtOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        txtResend = (TextView) findViewById(R.id.txtResendSMS);
        VerifiedTxt = (TextView) findViewById(R.id.Verifiedtxt);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Check valid
                final ProgressDialog dialog = new ProgressDialog(SMSVerificationOTP.this);

                final Intent intent = getIntent();
                final String[] Res = new String[1];
                final String otp = edtOTP.getText().toString();
                final String url = "http://www.kodyac.tech/scripts/VerifySMSOTP.php";
                dialog.setIndeterminate(true);
                dialog.setTitle("Verifying OTP");
                dialog.setMessage("Please wait while we verify your OTP");
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                        intent.getIntExtra("linkId",linkId);
                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("Response successful verification:", response);
                                        Res[0] = response;
                                        if (Res[0].contains("true")) {
                                            dialog.dismiss();
                                            Toast.makeText(SMSVerificationOTP.this, "Verified", Toast.LENGTH_SHORT).show();
                                            VerifiedTxt.setText("VERIFIED");
                                        }
                                    }
                                },
                                new Response.ErrorListener() {


                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("Error.Response", error.getLocalizedMessage());
                                    }
                                }) {
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("otp", otp );
                                params.put("linkId", Integer.toString(linkId));
                                Log.d("Shobhit", otp + " " + linkId);
                                return params;
                            }
                        };
                        queue.add(postRequest);
                    }
                }).start();



            }
        });

        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SMSVerificationOTP.this, "SMS Sent", Toast.LENGTH_SHORT).show();
                final Intent intent = getIntent();
                final String url = "http://www.kodyac.tech/scripts/SendSMSOTP.php";

                final String phone = intent.getStringExtra("phone");
                Log.d("Shobhit",phone);
                SMSSendRunnable Send_SMS = new SMSSendRunnable(getApplicationContext(), url, Lid, phone);
                Thread T = new Thread(Send_SMS);
                T.start();
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
