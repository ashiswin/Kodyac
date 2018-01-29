package com.ashiswin.kodyac;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class VerificationMethodsActivity extends AppCompatActivity {
    private static final int INTENT_VERIFICATION = 0;

    // TODO: REMOVE DUMMY DATA
    String[] methods = new String[] {"SMS Verification", "NRIC Verification", "Biometric Verification"};
    boolean[] completion = new boolean[] {false, false, false};

    VerificationAdapter adapter;

    ListView lstMethods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_methods);

        getSupportActionBar().setTitle("Verification Steps");

        lstMethods = (ListView) findViewById(R.id.lstMethods);

        adapter = new VerificationAdapter();
        lstMethods.setAdapter(adapter);

        lstMethods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(methods[position].equals("SMS Verification")) {
                    Intent smsIntent = new Intent(VerificationMethodsActivity.this, SMSVerificationNumber.class);
                    smsIntent.putExtra("methodId", position);
                    startActivityForResult(smsIntent, INTENT_VERIFICATION);
                }
                else if(methods[position].equals("NRIC Verification")) {
                    Intent nricIntent = new Intent(VerificationMethodsActivity.this, NRICVerificationCard.class);
                    nricIntent.putExtra("methodId", position);
                    startActivityForResult(nricIntent, INTENT_VERIFICATION);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_VERIFICATION && resultCode == RESULT_OK) {
            completion[data.getIntExtra("methodId", 0)] = true;
            adapter.notifyDataSetChanged();
        }
    }

    class VerificationAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return methods.length;
        }

        @Override
        public Object getItem(int position) {
            return methods[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView;

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                itemView = inflater.inflate(R.layout.verification_method_item, parent, false);
            }
            else {
                itemView = convertView;
            }

            ((TextView) itemView.findViewById(R.id.text)).setText(methods[position]);
            if(completion[position]) {
                ((ImageView) itemView.findViewById(R.id.imgStatus)).setImageResource(R.drawable.ic_done_black_24dp);
            }
            else {
                ((ImageView) itemView.findViewById(R.id.imgStatus)).setImageResource(R.drawable.ic_chevron_right_black_24dp);
            }

            return itemView;
        }
    }
}
