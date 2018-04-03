package com.ashiswin.kodyac;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Iterator;
import java.util.Map;

public class VerificationMethodsActivity extends AppCompatActivity {
    private static final String TAG = "VMActivity";
    private static final int INTENT_VERIFICATION = 0;

    MainApplication m;

    VerificationAdapter adapter;
    ListView lstMethods;

    Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_methods);
        getSupportActionBar().hide();

        m = (MainApplication) getApplicationContext();

        lstMethods = (ListView) findViewById(R.id.lstMethods);
        btnComplete = (Button) findViewById(R.id.btnComplete);

        adapter = new VerificationAdapter();
        lstMethods.setAdapter(adapter);

        lstMethods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(m.methodNames[position]) {
                    case "sms":
                        Intent smsIntent = new Intent(VerificationMethodsActivity.this, SMSVerificationNumberActivity.class);
                        startActivityForResult(smsIntent, INTENT_VERIFICATION);
                        break;
                    case "myinfo":
                        Intent myinfoIntent = new Intent(VerificationMethodsActivity.this, BasicInformationVerificationActivity.class);
                        startActivityForResult(myinfoIntent, INTENT_VERIFICATION);
                        break;
                    case "nric":
                        Intent photoIntent = new Intent(VerificationMethodsActivity.this, PhotoVerificationNRICActivity.class);
                        startActivityForResult(photoIntent, INTENT_VERIFICATION);
                        break;
                    case "video":
                        Intent videoIntent = new Intent(VerificationMethodsActivity.this, VideoVerificationNRICActivity.class);
                        startActivityForResult(videoIntent, INTENT_VERIFICATION);
                        break;
                }
            }
        });

        reloadList();

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(VerificationMethodsActivity.this);
                dialog.setTitle("Completing KYC");
                dialog.setMessage("Please wait while we complete your KYC session");
                dialog.setIndeterminate(true);
                dialog.show();

                StringRequest postRequest = new StringRequest(Request.Method.POST, MainApplication.SERVER_URL + "CompleteKYC.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject res = new JSONObject(response);
                                    dialog.dismiss();
                                    if (res.getBoolean("success")) {
                                        Intent verificationIntent = new Intent(VerificationMethodsActivity.this, CompletionActivity.class);
                                        startActivity(verificationIntent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(VerificationMethodsActivity.this, res.getString("message"), Toast.LENGTH_SHORT).show();
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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_VERIFICATION && resultCode == RESULT_OK) {
            reloadList();
        }
    }

    public void reloadList() {
        adapter.notifyDataSetChanged();

        Iterator<Map.Entry<String, Boolean>> it = m.methods.entrySet().iterator();
        boolean complete = true;

        while(it.hasNext()) {
            Map.Entry<String, Boolean> e = it.next();
            if(!e.getValue()) {
                complete = false;
                break;
            }
        }

        btnComplete.setEnabled(complete);
    }
    class VerificationAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return m.methodNames.length;
        }

        @Override
        public Object getItem(int position) {
            return m.methodNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("LongLogTag")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(R.layout.verification_method_item, parent, false);
            } else {
                itemView = convertView;
            }

            String name;

            switch (m.methodNames[position]) {
                case "sms":
                    name = "SMS Verification";
                    ((ImageView) itemView.findViewById(R.id.textIcon)).setImageDrawable(getDrawable(R.drawable.smsverifyicon));
                    break;
                case "myinfo":
                    name = "Basic Information Verification";
                    ((ImageView) itemView.findViewById(R.id.textIcon)).setImageDrawable(getDrawable(R.drawable.icverifyicon));
                    break;
                case "nric":
                    name = "Photo Verification";
                    ((ImageView) itemView.findViewById(R.id.textIcon)).setImageDrawable(getDrawable(R.drawable.faceverifyicon));
                    break;
                case "video":
                    name = "Video Verification";
                    ((ImageView) itemView.findViewById(R.id.textIcon)).setImageDrawable(getDrawable(R.drawable.biometricverifyicon));
                    break;
                default:
                    name = "Unknown Verification Method";
                    Log.e(TAG, m.methodNames[position]);
                    break;
            }

            ((TextView) itemView.findViewById(R.id.text)).setText(name);
            if(m.methods.get(m.methodNames[position])) {
                itemView.setBackgroundColor(Color.parseColor("#5cb85c"));
                ((TextView) itemView.findViewById(R.id.text)).setTextColor(Color.parseColor("#ECEFF1"));
                ((ImageView) itemView.findViewById(R.id.imgStatus)).setImageDrawable(getDrawable(R.drawable.tickverifiedeach));
            }

            return itemView;
        }
    }
}
