package com.example.asthafood;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ReportFragment;

import com.example.asthafood.activity.AssignProductActivity;
import com.example.asthafood.activity.NewShopKeeperEntry;
import com.example.asthafood.activity.ProductSellActivity;
import com.example.asthafood.activity.RequestProductSubmitActivity;
import com.example.asthafood.activity.SellReportActivity;
import com.example.asthafood.bean.GlobalStore;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private LinearLayout assign_product,Btn_add_shopkeeper,Btn_ll_activity_main_sell_report,Btn_ll_activity_main_sell_product,Btn_ll_activity_main_assign_product;
    private TextView user_naem,UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViewReferences();
        bindEventHandlers();

        user_naem.setText("Welcome "+GlobalStore.GlobalValue.getUserOriginalName());  //
        UserId.setText(GlobalStore.GlobalValue.getUserName());


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Toast.makeText(this, "Already in HomePage", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.nav_report) {
                Intent i = new Intent(MainActivity.this, SellReportActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
            return true;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_assign_product) {
                Toast.makeText(this, "Already in Home", Toast.LENGTH_SHORT).show();
                /*Intent i = new Intent(MainActivity.this, RequestProductSubmitActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);*/
               // finish();
            } else if (id == R.id.nav_request_product) {

            } else if (id == R.id.nav_sell) {
                Intent i = new Intent(MainActivity.this, SellReportActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
              //  Toast.makeText(this, "Sell", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_report) {
                Toast.makeText(this, "Report", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_return) {
                Toast.makeText(this, "Return", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    private void bindEventHandlers() {
        assign_product.setOnClickListener(this);
        Btn_add_shopkeeper.setOnClickListener(this);
        Btn_ll_activity_main_sell_report.setOnClickListener(this);
        Btn_ll_activity_main_sell_product.setOnClickListener(this);
        Btn_ll_activity_main_assign_product.setOnClickListener(this);
    }

    private void setViewReferences() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        assign_product=findViewById(R.id.ll_activity_main_assign_product);
        user_naem=findViewById(R.id.tv_activity_main_welcome_message);
        UserId=findViewById(R.id.tv_activity_main_employee_code);
        Btn_add_shopkeeper=findViewById(R.id.add_shopkeeper);
        Btn_ll_activity_main_sell_report=findViewById(R.id.ll_activity_main_sell_report);
        Btn_ll_activity_main_sell_product=findViewById(R.id.ll_activity_main_sell_product);
        Btn_ll_activity_main_assign_product=findViewById(R.id.ll_activity_main_assign_product);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

       if (v==assign_product){
          // Toast.makeText(this, "Assign Product", Toast.LENGTH_SHORT).show();

           Intent intent=new Intent(MainActivity.this, AssignProductActivity.class);
           startActivity(intent);
           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
           finish();


       } else if (v==UserId) {


       } else if (v==Btn_add_shopkeeper) {
           Intent intent=new Intent(MainActivity.this, NewShopKeeperEntry.class);
           startActivity(intent);
           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
           finish();

       } else if (v==Btn_ll_activity_main_sell_product) {

           Intent intent=new Intent(MainActivity.this, ProductSellActivity.class);
           startActivity(intent);
           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
           finish();
       } else if (v==Btn_ll_activity_main_sell_report) {
           Intent intent=new Intent(MainActivity.this, SellReportActivity.class);
           startActivity(intent);
           overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
           finish();

       }

    }
}
