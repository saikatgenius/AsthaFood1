package com.example.asthafood;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
    private AppCompatButton btn_Logout;
    private LinearLayout assign_product,Btn_add_shopkeeper,Btn_ll_activity_main_sell_report,Btn_ll_activity_main_sell_product,Btn_ll_activity_main_assign_product;
    private TextView user_naem,UserId;
    private SharedPreferences sharedPreferencesarranger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViewReferences();
        bindEventHandlers();

        user_naem.setText("Welcome "+GlobalStore.GlobalValue.getUserOriginalName());  //
        UserId.setText(GlobalStore.GlobalValue.getUserName());
        sharedPreferencesarranger = getSharedPreferences("ARRANGERLOGIN", Context.MODE_PRIVATE);

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
            } else if (id == R.id.nav_sell) {
                Intent i = new Intent(MainActivity.this, ProductSellActivity.class);
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
            } else if (id == R.id.nav_sell) {
                Intent intent=new Intent(MainActivity.this, ProductSellActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            } else if (id == R.id.nav_report) {
                Intent i = new Intent(MainActivity.this, SellReportActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
              //  Toast.makeText(this, "Sell", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_contact_us) {
                Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show();
            }else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
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
        btn_Logout.setOnClickListener(this);

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
        btn_Logout=findViewById(R.id.btnLogout);

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

       }else if (v==btn_Logout) {

           showLogoutDialog();



       }

    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout Confirmation");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferencesarranger.edit();
                editor.clear(); // Removes all saved data
                editor.apply();

                // Redirect to Login Page
                Intent intent = new Intent(MainActivity.this, LoginOptionsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear backstack
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Close the dialog if "No" is clicked
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
