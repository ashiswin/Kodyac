package com.ashiswin.kodyac;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//TODO: Make barcode and OCR go into the same results page????-jingyun

public class NRICBarcodeActivity extends AppCompatActivity {

    private Button startScan;
    private TextView barcodeNum;
    private TextView detailsDisplay;
    private final String endPoint="GetMyInfo.php";
    private final String ERROR_MSG="Deatils could not be obtained. Please ensure you have a SingPass Account.";
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nricbarcode);

        getSupportActionBar().setTitle("Scan NRIC");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startScan = (Button) findViewById(R.id.scan_bttn);
        barcodeNum = (TextView) findViewById(R.id.barcode_num);
        detailsDisplay = (TextView) findViewById(R.id.barcode_details);

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
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"?nric="+nricInput,
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
                }) {
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("nric", nricInput);
                        Log.d("posted", nricInput);
                        return params;
                    }
                };
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
                    detailsDisplay.setText(ERROR_MSG);
                }else{
                    String name = details.getString("name");
                    String sex = details.getString("sex");
                    String race = details.getString("race");
                    String dob = details.getString("dob");
                    String address = details.getString("address");
                    detailsDisplay.setText("name: "+name+"\n"
                            +"sex: "+sex+"\n"
                            +"race: "+race+"\n"
                            +"dob: "+dob+"\n"
                            +"address: "+address+"\n");
                }
            }else{
                //cannot log into MyInfo API
                detailsDisplay.setText(ERROR_MSG);
            }


        } catch(Exception e){
            e.printStackTrace();
            Log.e("NRICBarcode","Json Parsing exception");
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
