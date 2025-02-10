package com.example.asthafood;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateAppActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTv_msgHeading,mTv_msgBody1,mTv_msgBody2,mTv_msgBody3;
    private Button mBtn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);

        mTv_msgHeading=findViewById(R.id.tv_app_update_heading);
        mTv_msgBody1=findViewById(R.id.tv_app_update_text1);
        mTv_msgBody2=findViewById(R.id.tv_app_update_text2);
        mTv_msgBody3=findViewById(R.id.tv_app_update_text3);
        mBtn_ok=findViewById(R.id.btn_activity_update_app_ok);


        mTv_msgHeading.setText("A New Version of this app is available");
        //mTv_msgBody1.setText("Your app version is "+ com.geniustechnoindia.ichasathinidhi.BuildConfig.VERSION_NAME);
        //mTv_msgBody2.setText("Latest version is "+SplashScreen.updated_app_ver);
        mTv_msgBody3.setText("Please update your app to continue");
        mBtn_ok.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if(v==mBtn_ok){
            //finish();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse((Uri.parse("market://details?id=")+getPackageName()))));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Uri.parse("https://play.google.com/store/apps/details?id=")+getPackageName())));
            }
        }
    }
}