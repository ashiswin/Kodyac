package com.ashiswin.kodyac;

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

import java.util.Iterator;
import java.util.Map;

public class VerificationMethodsActivity extends AppCompatActivity {
    private static final String TAG = "VerificationMethodsActivity";
    private static final int INTENT_VERIFICATION = 0;

    MainApplication m;

    VerificationAdapter adapter;
    ListView lstMethods;

    Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_methods);

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
                        Intent smsIntent = new Intent(VerificationMethodsActivity.this, SMSVerificationNumber.class);
                        startActivityForResult(smsIntent, INTENT_VERIFICATION);
                        break;
                    case "myinfo":
                        Intent nricbarcodeIntent = new Intent(VerificationMethodsActivity.this, BasicInformationVerificationActivity.class);
                        startActivityForResult(nricbarcodeIntent, INTENT_VERIFICATION);
                        break;
                    case "nric":
                        Intent emasIntent = new Intent(VerificationMethodsActivity.this, PhotoVerificationNRICActivity.class);
                        startActivityForResult(emasIntent, INTENT_VERIFICATION);
                        break;
                    case "video":
                        Intent openCVtestIntent = new Intent(VerificationMethodsActivity.this, VideoVerificationVideoActivity.class);
                        startActivityForResult(openCVtestIntent, INTENT_VERIFICATION);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_VERIFICATION && resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();

            Iterator<Map.Entry<String, Boolean>> it = m.methods.entrySet().iterator();
            boolean complete = false;

            while(it.hasNext()) {
                Map.Entry<String, Boolean> e = it.next();
                if(!e.getValue()) {
                    complete = false;
                    break;
                }
            }

            btnComplete.setEnabled(complete);
        }
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
                    break;
                case "myinfo":
                    name = "Basic Information Verification";
                    break;
                case "nric":
                    name = "Photo Verification";
                    break;
                case "video":
                    name = "Video Verification";
                    break;
                default:
                    name = "Unknown Verification Method";
                    Log.e(TAG, m.methodNames[position]);
                    break;
            }

            ((TextView) itemView.findViewById(R.id.text)).setText(name);
            if(m.methods.get(m.methodNames[position])) {
                itemView.setBackgroundColor(Color.parseColor("#5cb85c"));
                ((TextView) itemView.findViewById(R.id.text)).setTextColor(Color.parseColor("#FFFFFF"));
                ((ImageView) itemView.findViewById(R.id.imgStatus)).setImageDrawable(getDrawable(R.drawable.ic_chevron_right_white_24dp));
            }

            return itemView;
        }
    }
}
