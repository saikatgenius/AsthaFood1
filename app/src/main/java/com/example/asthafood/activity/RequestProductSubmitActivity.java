package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.AdapterItemCategory;
import com.example.asthafood.databinding.ActivityRequestProductSubmitBinding;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.ItemCategory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;

public class RequestProductSubmitActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    ActivityRequestProductSubmitBinding binding ;

    ItemCategory itemCategory;
    ArrayList<ItemCategory> arrayList = new ArrayList<>();

    AdapterItemCategory adapterItemCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestProductSubmitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.rvItemcategory.setLayoutManager(linearLayoutManager);

        mToolbar = findViewById(R.id.custom_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
       // mToolbarTitle.setText("Sell Product");
        mToolbarTitle.setText("Product Category");
        getAllCategory();




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                startActivity(new Intent(RequestProductSubmitActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getAllCategory(){
        final ProgressDialog progressDialog = new ProgressDialog(RequestProductSubmitActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetItemCategory()}");
                smt.execute();
                ResultSet rs = smt.getResultSet();
                while (rs.next()) {
                    itemCategory = new ItemCategory();
                    itemCategory.setCategoriName(rs.getString("CategoryName"));
                    itemCategory.setCategoriyNo(rs.getString("CategoryNo"));
                    arrayList.add(itemCategory);
                }

                adapterItemCategory = new AdapterItemCategory(RequestProductSubmitActivity.this, arrayList);
                binding.rvItemcategory.setAdapter(adapterItemCategory);
                progressDialog.dismiss();

            } else {
                progressDialog.dismiss();
                Log.d("bbc1", "isUpdateAvail: "+cn);
            }
        } catch (Exception ex) {
            progressDialog.dismiss();
            Log.d("bbc", "isUpdateAvail: "+ex);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RequestProductSubmitActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}