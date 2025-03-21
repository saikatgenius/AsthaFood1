package com.example.asthafood.IncActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asthafood.R;
import com.example.asthafood.activity.RequestCartItemActivity;
import com.example.asthafood.activity.RequestProductItems;
import com.example.asthafood.activity.RequestProductSubmitActivity;
import com.example.asthafood.adapters.CategoryItemsAdapter;
import com.example.asthafood.databinding.ActivityIncRequestProductItemsBinding;
import com.example.asthafood.databinding.ActivityRequestProductItemsBinding;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.ItemCategoryItems;
import com.google.android.exoplayer2.util.Log;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class IncRequestProductItems extends AppCompatActivity {

    ActivityIncRequestProductItemsBinding binding;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    String CategoryNo = "";

    ItemCategoryItems itemCategoryItems;
    ArrayList<ItemCategoryItems> arrayList = new ArrayList<>();
    CategoryItemsAdapter categoryItemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncRequestProductItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        CategoryNo = getIntent().getStringExtra("categoryNo");
        Log.e("CategoryNo",""+CategoryNo);
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

        mToolbarTitle.setText("Product Category Items ");
        getAllCategoryItem();

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IncRequestProductItems.this, IncRequestCartItemActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });


    }

    private void getAllCategoryItem() {
        final ProgressDialog progressDialog = new ProgressDialog(IncRequestProductItems.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_IncGetItemCategoryWise(?)}");
                smt.setString("@CategoryNo", CategoryNo);
                smt.execute();
                ResultSet rs = smt.getResultSet();
                while (rs.next()) {
                    itemCategoryItems = new ItemCategoryItems();
                    itemCategoryItems.setProductID(rs.getString("ItemID"));
                    itemCategoryItems.setProductName(rs.getString("ItemName"));
                    itemCategoryItems.setPrice(rs.getString("IncSalePrice"));
                    itemCategoryItems.setBatchNo(rs.getString("BatchNo"));
                    arrayList.add(itemCategoryItems);
                }
                categoryItemsAdapter = new CategoryItemsAdapter( arrayList,IncRequestProductItems.this);
                binding.rvItemcategory.setAdapter(categoryItemsAdapter);
                progressDialog.dismiss();

            } else {
                progressDialog.dismiss();
                android.util.Log.d("bbc1", "isUpdateAvail: "+cn);
            }
        } catch (Exception ex) {
            progressDialog.dismiss();
            android.util.Log.d("bbc", "isUpdateAvail: "+ex);
        }


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                startActivity(new Intent(IncRequestProductItems.this, IncRequestProductSubmitActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(IncRequestProductItems.this, IncRequestProductSubmitActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}