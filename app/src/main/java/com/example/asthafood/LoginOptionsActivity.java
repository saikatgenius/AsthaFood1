package com.example.asthafood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asthafood.activity.EmployeeLoginActivity;
import com.example.asthafood.dl.LoginManagement;

public class LoginOptionsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTv_memberLogin;
    private TextView mTv_associateLogin;
    private TextView mTv_adminLogin;
    private TextView mTv_aboutUS;

    private SharedPreferences sharedPreferencesArranger;
    private Boolean rememberStatusArranger = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_options);

        setViewReferences();
        bindEventHandlers();


        sharedPreferencesArranger = getSharedPreferences("ARRANGERLOGIN", Context.MODE_PRIVATE);
        rememberStatusArranger = sharedPreferencesArranger.getString("REMEMBER", "").equals("TRUE");

        SharedPreferences sharedPreferences_loginSelection=getSharedPreferences("LoginSelection", MODE_PRIVATE);
        String selection = sharedPreferences_loginSelection.getString("IsMemberLogin","");




    }

    private void setViewReferences() {
        // views
        mTv_memberLogin = findViewById(R.id.tv_login_options_activity_member_login);
        mTv_associateLogin = findViewById(R.id.tv_login_options_activity_associate_login);

        mTv_adminLogin = findViewById(R.id.tv_login_options_activity_admin_login);

        mTv_aboutUS = findViewById(R.id.tv_login_options_activity_about_company);
    }

    private void bindEventHandlers() {

        mTv_associateLogin.setOnClickListener(this);
        mTv_memberLogin.setOnClickListener(this);
        mTv_adminLogin.setOnClickListener(this);
        mTv_aboutUS.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mTv_associateLogin) {
            if (rememberStatusArranger) {

                LoginManagement um = new LoginManagement();
                String username = sharedPreferencesArranger.getString("USERNAME", "");
                String password = sharedPreferencesArranger.getString("PASSWORD", "");

                if (um.isLoginAgentSuccessful(username, password)) {
                    startActivity(new Intent(LoginOptionsActivity.this, MainActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    startActivity(new Intent(LoginOptionsActivity.this, EmployeeLoginActivity.class));
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }

            } else {
                startActivity(new Intent(LoginOptionsActivity.this, EmployeeLoginActivity.class));
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }



}