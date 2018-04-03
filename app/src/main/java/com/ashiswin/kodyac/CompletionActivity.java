package com.ashiswin.kodyac;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CompletionActivity extends AppCompatActivity {
    TextView txtCountdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completion);

        getSupportActionBar().hide();

        txtCountdown = (TextView) findViewById(R.id.txtCountdown);

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtCountdown.setText((millisUntilFinished / 1000) + "");
            }

            public void onFinish() {
                finish();
            }
        }.start();
    }
}
