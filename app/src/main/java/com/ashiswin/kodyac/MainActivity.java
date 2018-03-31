package com.ashiswin.kodyac;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {
    TextView txtWelcome;
    ImageView imgLogo;
    Button btnBegin;

    MainApplication m;

    private String linkEndPoint = "GetLink.php";
    private String companyEndPoint = "GetCompany.php";

    ProgressDialog d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        Resources res = getResources();
        String text = res.getString(R.string.welcome_string, "IBM");

        txtWelcome = (TextView) findViewById(R.id.txtWelcome);
        imgLogo = (ImageView) findViewById(R.id.imgLogo);
        btnBegin = (Button) findViewById(R.id.btnBegin);

        m = (MainApplication) getApplicationContext();

        //make Button un-clickable until relevant information is loaded
        btnBegin.setEnabled(false);

        txtWelcome.setText(text);
        imgLogo.setImageResource(R.drawable.ibmkodyac);
        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Beginning KYC");
                dialog.setMessage("Please wait while we load your KYC session");
                dialog.setIndeterminate(true);
                dialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringRequest postRequest = new StringRequest(Request.Method.POST, MainApplication.SERVER_URL + "BeginKYC.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject res = new JSONObject(response);
                                            dialog.dismiss();
                                            if (res.getBoolean("success")) {
                                                Intent verificationIntent = new Intent(MainActivity.this, VerificationMethodsActivity.class);
                                                startActivity(verificationIntent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(MainActivity.this, res.getString("message"), Toast.LENGTH_SHORT).show();
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
                                params.put("id", Integer.toString(m.linkId));
                                return params;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        queue.add(postRequest);
                    }
                }).start();
            }
        });
        handleAppIntent();
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        handleAppIntent();
    }

    private void handleAppIntent() {
        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if(appLinkData == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog);
            builder.setTitle("KodYaC Error");
            builder.setMessage("Please select a KYC link to launch KodYaC");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
        else {
            //extract company ID
            String companyIDString = appLinkData.getQueryParameter("id");
            int linkID = Integer.valueOf(companyIDString);
            d = new ProgressDialog(MainActivity.this);
            d.setTitle("Loading KYC");
            d.setMessage("Please wait while we load this KYC session");
            d.setIndeterminate(true);
            d.show();
            getLink(linkID);

            m.linkId = linkID;
        }
    }

    //get information about company and KYC methods using  HTTP GET Request
    private void getLink(final int idLink) {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = MainApplication.SERVER_URL + linkEndPoint;

        // Request a string response from the provided URL.
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"?id="+idLink,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("Link ID", "Response is " + response);
                                //convert the string to a json object so i easily parse it
                                try {
                                    JSONObject responseJson = new JSONObject(response);
                                    //successfully get linkID from HTTP GET
                                    if (responseJson.getBoolean("success")){
                                        JSONObject linkJson = responseJson.getJSONObject("link");
                                        if(linkJson.getString("status").equals("completed")) {
                                            d.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog);
                                            builder.setTitle("KodYaC Error");
                                            builder.setMessage("This KYC has already been completed");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    finish();
                                                }
                                            });
                                            builder.setCancelable(false);
                                            builder.show();
                                        }
                                        m.companyId = linkJson.getInt("companyId");
                                        m.name = linkJson.getString("name");
                                        m.address = linkJson.getString("address");
                                        m.dob = linkJson.getString("dob");
                                        m.contact = linkJson.getString("contact");
                                        m.nationality = linkJson.getString("nationality");
                                        m.nric = linkJson.getString("nric");
                                        m.sex = linkJson.getString("sex");
                                        m.race = linkJson.getString("race");

                                        m.methods = new HashMap<>();

                                        String[] completedMethods = linkJson.getString("completedMethods").split("\\|");
                                        for(String s : completedMethods) {
                                            m.methods.put(s, true);
                                        }
                                        //Log.e("Company ID", "company ID obtained is " + linkJson.getInt("companyId"));
                                        getCompany(m.companyId);
                                    }else{
                                        String errorMsg = responseJson.getString("message");
                                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                                    }
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
                        params.put("id", Integer.toString(idLink));
                        Log.d("posted", Integer.toString(idLink));
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }).start();
    }

    private void getCompany(final int idCompany) {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = MainApplication.SERVER_URL + companyEndPoint;

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"?id=" + idCompany,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Company ID", "Response is " + response);
                        //convert the string to a json object so i easily parse it
                        try {
                            JSONObject responseJson = new JSONObject(response);
                            //successfully get linkID from HTTP GET
                            if (responseJson.getBoolean("success")){
                                JSONObject companyJson = responseJson.getJSONObject("company");
                                Log.e("Company ID", companyJson.toString());
                                String[] methods = companyJson.getString("methods").split("\\|");
                                m.methodNames = methods;
                                for(String s : methods) {
                                    if(!m.methods.containsKey(s)) {
                                        m.methods.put(s, false);
                                    }
                                }
                                btnBegin.setEnabled(true);
                            }else{
                                String errorMsg = responseJson.getString("message");
                                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                            d.dismiss();
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
                params.put("id", Integer.toString(idCompany));
                Log.d("posted", Integer.toString(idCompany));
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



}
