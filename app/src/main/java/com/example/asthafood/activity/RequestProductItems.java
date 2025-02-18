package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.AdapterItemCategory;
import com.example.asthafood.adapters.CategoryItemsAdapter;
import com.example.asthafood.databinding.ActivityRequestProductItemsBinding;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.ItemCategory;
import com.example.asthafood.mssql.models.ItemCategoryItems;
import com.google.android.exoplayer2.util.Log;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class RequestProductItems extends AppCompatActivity {

    ActivityRequestProductItemsBinding binding;

    String CategoryNo = "";

    ItemCategoryItems itemCategoryItems;
    ArrayList<ItemCategoryItems> arrayList = new ArrayList<>();
    CategoryItemsAdapter categoryItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestProductItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        CategoryNo = getIntent().getStringExtra("categoryNo");
        Log.e("CategoryNo",""+CategoryNo);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.rvItemcategory.setLayoutManager(linearLayoutManager);
        getAllCategoryItem();

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RequestProductItems.this, RequestCartItemActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });



    }

    private void getAllCategoryItem() {
        final ProgressDialog progressDialog = new ProgressDialog(RequestProductItems.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetItemCategoryWise(?)}");
                smt.setString("@CategoryNo", CategoryNo);
                smt.execute();
                ResultSet rs = smt.getResultSet();
                while (rs.next()) {
                    itemCategoryItems = new ItemCategoryItems();
                    itemCategoryItems.setProductID(rs.getString("ItemID"));
                    itemCategoryItems.setProductName(rs.getString("ItemName"));
                    arrayList.add(itemCategoryItems);
                }
                categoryItemsAdapter = new CategoryItemsAdapter( arrayList,RequestProductItems.this);
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RequestProductItems.this, RequestProductSubmitActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}