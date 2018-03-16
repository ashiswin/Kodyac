package com.ashiswin.kodyac;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class VerificationMethodsActivity extends AppCompatActivity {
    private static final int INTENT_VERIFICATION = 0;

    String[] methods = new String[] {"SMS Verification", "Basic Information Verification", "NRIC Verification", "Video Verification"};
    boolean[] completion = new boolean[] {false, false, false, false};

    VerificationAdapter adapter;
    RecyclerView lstMethods;
    RecyclerView.LayoutManager layoutManager;

    Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_methods);

        lstMethods = (RecyclerView) findViewById(R.id.lstMethods);
        btnComplete = (Button) findViewById(R.id.btnComplete);

        adapter = new VerificationAdapter();
        layoutManager = new GridLayoutManager(VerificationMethodsActivity.this, 1);
        lstMethods.setLayoutManager(layoutManager);
        lstMethods.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INTENT_VERIFICATION && resultCode == RESULT_OK) {
            completion[data.getIntExtra("methodId", 0)] = true;
            adapter.notifyDataSetChanged();

            boolean complete = true;
            for(boolean c : completion) {
                if(!c) {
                    complete = false;
                    break;
                }
            }

            btnComplete.setEnabled(complete);
        }
    }

    class VerificationAdapter extends RecyclerView.Adapter<VerificationAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.verification_method_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText(methods[position]);

            if(completion[position]) {
                holder.status.setImageResource(R.drawable.ic_done_black_24dp);
            }
            else {
                holder.status.setImageResource(R.drawable.ic_chevron_right_black_24dp);
            }
            holder.position = position;
        }

        @Override
        public int getItemCount() {
            return methods.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView text;
            public ImageView status;
            public int position;

            public ViewHolder(View v) {
                super(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = ((TextView) v.findViewById(R.id.text)).getText().toString();

                        if (text.equals("SMS Verification")) {
                            Intent smsIntent = new Intent(VerificationMethodsActivity.this, SMSVerificationNumber.class);
                            smsIntent.putExtra("methodId", position);
                            startActivityForResult(smsIntent, INTENT_VERIFICATION);
                        } else if (text.equals("OLD NRIC test")) {
                            Intent nricIntent = new Intent(VerificationMethodsActivity.this, NRICVerificationCard.class);
                            nricIntent.putExtra("methodId", position);
                            startActivityForResult(nricIntent, INTENT_VERIFICATION);
                        } else if (text.equals("NRIC Verification")){
                            Intent nricbarcodeIntent = new Intent(VerificationMethodsActivity.this, NRICVerificationMethodActivity.class);
                            nricbarcodeIntent.putExtra("methodId", position);
                            startActivityForResult(nricbarcodeIntent, INTENT_VERIFICATION);
                        }else if (text.equals("OpenCVTest")){
                            Intent openCVtestIntent = new Intent(VerificationMethodsActivity.this, OpenCVActivity.class);
                            openCVtestIntent.putExtra("methodId", position);
                            startActivityForResult(openCVtestIntent, INTENT_VERIFICATION);
                        }else if (text.equals("FaceVerification")){
                            Intent openCVtestIntent = new Intent(VerificationMethodsActivity.this, FaceVerificationActivity.class);
                            openCVtestIntent.putExtra("methodId", position);
                            startActivityForResult(openCVtestIntent, INTENT_VERIFICATION);
                        }
                    }
                });
                text = (TextView) v.findViewById(R.id.text);
                status = (ImageView) v.findViewById(R.id.imgStatus);
            }
        }

    }
}
