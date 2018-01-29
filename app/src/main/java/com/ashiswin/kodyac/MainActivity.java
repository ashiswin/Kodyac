package com.ashiswin.kodyac;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView txtWelcome;
    ImageView imgLogo;
    Button btnBegin;

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

        txtWelcome.setText(text);
        imgLogo.setImageResource(R.drawable.ibm);
        btnBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent verificationIntent = new Intent(MainActivity.this, VerificationMethodsActivity.class);
                startActivity(verificationIntent);
                finish();
            }
        });
    }
}
