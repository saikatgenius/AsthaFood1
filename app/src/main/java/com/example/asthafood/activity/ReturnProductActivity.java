package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.example.asthafood.adapters.SellProductDetailsAdapter;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.databinding.ActivityReturnProductBinding;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.SellProductDetailsModel;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ReturnProductActivity extends AppCompatActivity {

    ActivityReturnProductBinding binding;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    SellProductDetailsModel sellProductDetailsModel;
    private ArrayList<SellProductDetailsModel> arrayList=new ArrayList<>();
    SellProductDetailsAdapter sellProductDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReturnProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mToolbar = findViewById(R.id.custom_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbarTitle.setText("Return Product");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.rvproductDetails.setLayoutManager(linearLayoutManager);
        getProducts(GlobalStore.GlobalValue.getUserName());
        binding.getTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubmitData(arrayList);


            }
        });




    }

    private void SubmitData(ArrayList<SellProductDetailsModel> arrayList) {
        String collectionList = "";
        for (int i = 0 ; i<arrayList.size();i++){
                collectionList +=
                        arrayList.get(i).getProductID()+","+arrayList.get(i).getVoucherNo()+"," +arrayList.get(i).getBatchNo()+";";
                Log.d("ergergerg" + "", collectionList);


        }
        final ProgressDialog progressDialog = new ProgressDialog(ReturnProductActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call USP_ADROID_INSERT_RETURN_PRO(?,?,?)}");
                smt.setString("@UserName",GlobalStore.GlobalValue.getUserName());
                smt.setString("@CollectionList",collectionList);
                smt.registerOutParameter("@ErrorCode",java.sql.Types.INTEGER);
                smt.executeUpdate();
                int ReturnERRORCode  = smt.getInt("@ErrorCode");
                if (ReturnERRORCode==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReturnProductActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Successful");
                    builder.setMessage("Ruturn Product Successfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(ReturnProductActivity.this, MainActivity.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                        }
                    }).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReturnProductActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Unable to enter Data");
                    builder.setMessage("Ruturn Product Send UnSuccessfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(ReturnProductActivity.this, MainActivity.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                        }
                    }).show();
                }
            } else {
                progressDialog.dismiss();
                Log.d("bbc1", "isUpdateAvail: "+cn);
            }
        } catch (Exception ex) {
            progressDialog.dismiss();
            Log.d("bbc", "isUpdateAvail: "+ex);
        }
    }

    private void getProducts(String userName) {
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetReturnItems(?)}");
                smt.setString("@UserName",userName);
                smt.execute();
                ResultSet rs = smt.getResultSet();
                if (rs.isBeforeFirst()){
                    while (rs.next()) {
                        sellProductDetailsModel = new SellProductDetailsModel();
                        sellProductDetailsModel.setProductID(rs.getString("ProductID"));
                        sellProductDetailsModel.setBatchNo(rs.getString("BatchNo"));
                        sellProductDetailsModel.setAssingQnty(rs.getString("AssingQnty"));
                        sellProductDetailsModel.setSellQunty(rs.getString("SellQnty"));
                        sellProductDetailsModel.setReturnQunt(rs.getString("ReturnQnty"));
                        sellProductDetailsModel.setPrice(rs.getString("EmployeeSellPrice"));
                        sellProductDetailsModel.setRemainingQuntity(rs.getInt("RemianingQnty"));
                        sellProductDetailsModel.setProductName(rs.getString("ProductName"));
                        sellProductDetailsModel.setMRP(rs.getString("MRP"));
                        sellProductDetailsModel.setExpiryDate(rs.getString("ExpiryDate"));
                        sellProductDetailsModel.setGST(rs.getString("TotalGST"));
                        sellProductDetailsModel.setVoucherNo(rs.getString("VoucherNo"));

                        sellProductDetailsModel.setIsNew(rs.getString("NewProduct"));
                        arrayList.add(sellProductDetailsModel);
                    }
                    sellProductDetailsAdapter = new SellProductDetailsAdapter( arrayList,ReturnProductActivity.this,"ReturnProduct");
                    binding.rvproductDetails.setAdapter(sellProductDetailsAdapter);
                }else{
                    binding.errorMsg.setVisibility(View.VISIBLE);
                    binding.getTotal.setVisibility(View.GONE);
                    binding.rvproductDetails.setVisibility(View.GONE);
                }
            } else {

                Log.d("bbc1", "isUpdateAvail: "+cn);
            }
        } catch (Exception ex) {
            Log.d("bbc", "isUpdateAvail: "+ex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                startActivity(new Intent(ReturnProductActivity.this, MainActivity.class));
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
        startActivity(new Intent(ReturnProductActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}