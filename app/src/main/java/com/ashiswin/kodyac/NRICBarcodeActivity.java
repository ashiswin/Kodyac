package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//TODO: Make barcode and OCR go into the same results page????-jingyun

public class NRICBarcodeActivity extends AppCompatActivity {
    private Button startScan;
    private Button btnConfirm;
    private TextView barcodeNum;
    private TextView nameText;
    private TextView raceText;
    private TextView sexText;
    private TextView countryText;
    private TextView dobText;
    private TextView addressText;
    private final String endPoint = "GetMyInfo.php";
    private final String ERROR_MSG = "Deatils could not be obtained. Please ensure you have a SingPass Account.";
    private JSONObject jsonObject;

    MainApplication m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nricbarcode);

        getSupportActionBar().setTitle("Scan NRIC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startScan = (Button) findViewById(R.id.scan_bttn);
        barcodeNum = (TextView) findViewById(R.id.barcode_num);
        nameText  = (TextView) findViewById(R.id.txtName);
        raceText = (TextView) findViewById(R.id.txtRace);
        sexText = (TextView) findViewById(R.id.txtSex);
        countryText = (TextView) findViewById(R.id.txtCountryBirth);
        dobText = (TextView) findViewById(R.id.txtDOB);
        addressText = (TextView) findViewById(R.id.txtAddress);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        m = (MainApplication) getApplicationContext();

        if(m.methods.get("myinfo")) {
            startScan.setEnabled(false);
            btnConfirm.setVisibility(View.GONE);

            barcodeNum.setText(m.nric);
            nameText.setText(m.name);
            raceText.setText(m.race);
            sexText.setText(m.sex);
            countryText.setText(m.nationality);
            dobText.setText(m.dob);
            addressText.setText(m.address);
        }
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

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.nric = barcodeNum.getText().toString();
                m.name = nameText.getText().toString();
                m.sex = sexText.getText().toString();
                m.race = raceText.getText().toString();
                m.dob = dobText.getText().toString();
                m.address = addressText.getText().toString();
                m.nationality = countryText.getText().toString();

                final ProgressDialog dialog = new ProgressDialog(NRICBarcodeActivity.this);

                final String url = MainApplication.SERVER_URL + "VerifyMyInfo.php";
                dialog.setIndeterminate(true);
                dialog.setTitle("Verifying Info");
                dialog.setMessage("Please wait while we verify your info");
                dialog.show();
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res = new JSONObject(response);
                                    dialog.dismiss();
                                    if (res.getBoolean("success")) {
                                        completeMethod();
                                    }
                                    else {
                                        Toast.makeText(NRICBarcodeActivity.this, res.getString("message"), Toast.LENGTH_SHORT).show();
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
                        params.put("name", m.name);
                        params.put("nric", m.nric);
                        params.put("address", m.address);
                        params.put("nationality", m.nationality);
                        params.put("dob", m.dob);
                        params.put("sex", m.sex);
                        params.put("race", m.race);
                        params.put("linkId", Integer.toString(m.linkId));
                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(postRequest);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        //once there is result output the barcode number
        if (scanResult!=null){
            String scanContent = scanResult.getContents();
            barcodeNum.setText(scanContent);
            sendGetMyInfo(scanContent);
        }else{
            Toast.makeText(this, "No scan result", Toast.LENGTH_SHORT).show();
        }

    }

    //send the barcode to server using a HTTP GET Request
    private void sendGetMyInfo(final String nricInput) {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url =getString(R.string.base_url)+endPoint;

        // Request a string response from the provided URL.
        //TODO: do i need threads????? -jingyun
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?nric=" + nricInput,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("NRIC Barcode", "Response is " + response);
                                //convert the string to a json object so i easily parse it
                                try {
                                    jsonObject = new JSONObject(response);
                                    displayResult(jsonObject);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e("JSON ERROR", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.getLocalizedMessage());
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }).start();



    }

    //parse json object obtained from HTTP GET method above
    private void displayResult(JSONObject jsonObject) {
        try{
            //currently everything reutrns true
            if (jsonObject.getBoolean("success")){
                JSONObject details = jsonObject.getJSONObject("details");
                /*"name":null,"sex":null,"race":null,"dob":null,"address":"  #-, S"*/
                if (details.isNull("name")||details.isNull("sex")||details.isNull("race")||details.isNull("dob")){
                    nameText.setText(ERROR_MSG);
                }
                else{
                    String name = details.getString("name").trim();
                    String sex = details.getString("sex").trim();
                    String race = details.getString("race").trim();
                    String dob = details.getString("dob").trim();
                    String address = details.getString("address").trim();
                    String nationality = details.getString("nationality").trim();
                    nameText.setText(name);
                    sexText.setText(sex);
                    raceText.setText(race);
                    dobText.setText(dob);
                    addressText.setText(address);
                    countryText.setText(nationality);

                    btnConfirm.setEnabled(true);
                }
            }
            else{
                //cannot log into MyInfo API
                nameText.setText(ERROR_MSG);
            }
        } catch(Exception e){
            e.printStackTrace();
            Log.e("NRICBarcode","Json Parsing exception");
        }
    }

    public void completeMethod() {
        final ProgressDialog dialog = new ProgressDialog(NRICBarcodeActivity.this);

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
                                m.methods.put("myinfo", true);
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
                params.put("method", "myinfo");
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
