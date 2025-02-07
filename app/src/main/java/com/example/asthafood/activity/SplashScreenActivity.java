package com.example.asthafood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.asthafood.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashScreenActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTv_splashVersion;
    private LinearLayout mLl_openGTech;
    private LinearLayout mLl_devByGen;
    public static String updated_app_ver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        setViewReferences();
        bindEventHandlers();

       /// mTv_splashVersion.setText("Version " + .);

        isUpdateAvail();

    }
    private void setViewReferences() {
        mTv_splashVersion = findViewById(R.id.splash_version);
        mLl_openGTech = findViewById(R.id.ll_open_genius_technology_links);
    }

    private void bindEventHandlers() {
        mTv_splashVersion.setOnClickListener(this);
        mLl_openGTech.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mLl_openGTech) {
            new OpenLinks(this).openGeniusTechnology();
        }
    }
    public void isUpdateAvail() {
        new GetDataParserArray(this, APILinks.IS_UPDATE_AVAILABLE + com.geniustechnoindia.sanglapMicro.BuildConfig.VERSION_NAME, true, new GetDataParserArray.OnGetResponseListener() {
            @Override
            public void onGetResponse(JSONArray response) {
                try {
                    if (response != null) {
                        JSONObject jsonObject = response.getJSONObject(0);
                        updated_app_ver = jsonObject.getString("VersionName");
                        if (jsonObject.getInt("isAvailable") == 1) {
                            callWaitMethod(true);
                        } else {
                            callWaitMethod(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void callWaitMethod(Boolean flag) {
        if (flag) {
            YoYo.with(Techniques.StandUp)
                    .duration(3000)
                    .repeat(0)
                    .playOn(findViewById(R.id.tv_splash_activity_title));

            int secondsDelayed = 2;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, UpdateAppActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }, secondsDelayed * 2000);
        } else {
            YoYo.with(Techniques.StandUp)
                    .duration(3000)
                    .repeat(0)
                    .playOn(findViewById(R.id.tv_splash_activity_title));

            int secondsDelayed = 2;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, LoginOptionsActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }, secondsDelayed * 2000);
        }
    }
}