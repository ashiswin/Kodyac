package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SMSVerificationOTPActivity extends AppCompatActivity {
    EditText edtOTP;
    Button btnVerify;
    TextView txtResend;

    MainApplication m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsverification_otp);
        getSupportActionBar().setTitle("Verify OTP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        edtOTP = (EditText) findViewById(R.id.edtOTP);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        txtResend = (TextView) findViewById(R.id.txtResendSMS);

        m = (MainApplication) getApplicationContext();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Check valid
                final ProgressDialog dialog = new ProgressDialog(SMSVerificationOTPActivity.this);

                final String otp = edtOTP.getText().toString();
                final String url = MainApplication.SERVER_URL + "VerifySMSOTP.php";
                dialog.setIndeterminate(true);
                dialog.setTitle("Verifying OTP");
                dialog.setMessage("Please wait while we verify your OTP");
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("Response successful verification:", response);
                                        dialog.dismiss();
                                        try {
                                            JSONObject res = new JSONObject(response);
                                            if (res.getBoolean("success")) {
                                                Toast.makeText(SMSVerificationOTPActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                                                m.methods.put("sms", true);
                                                m.contact = getIntent().getStringExtra("phone");
                                                completeMethod();
                                            }
                                            else {
                                                edtOTP.setError(res.getString("message"));
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
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
                                params.put("linkId", Integer.toString(m.linkId));
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
                Toast.makeText(SMSVerificationOTPActivity.this, "SMS Sent", Toast.LENGTH_SHORT).show();
                final Intent intent = getIntent();
                final String url = MainApplication.SERVER_URL + "SendSMSOTP.php";

                final String phone = intent.getStringExtra("phone");
                Log.d("Shobhit",phone);
                SMSSendRunnable Send_SMS = new SMSSendRunnable(getApplicationContext(), url, m.linkId, phone);
                Thread T = new Thread(Send_SMS);
                T.start();
            }
        });
    }

    public void completeMethod() {
        final ProgressDialog dialog = new ProgressDialog(SMSVerificationOTPActivity.this);

        final String url = MainApplication.SERVER_URL + "AddMethodCompletion.php";
        dialog.setIndeterminate(true);
        dialog.setTitle("Submitting Completion");
        dialog.setMessage("Please wait while we submit your completion");
        dialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if (res.getBoolean("success")) {
                                dialog.dismiss();

                                setResult(RESULT_OK);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("method", "sms");
                params.put("linkId", Integer.toString(m.linkId));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(postRequest);
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
