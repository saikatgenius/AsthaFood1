package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.SellProductDetailsAdapter;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.databinding.ActivityProductSellBinding;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.SellProductDetailsModel;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ProductSellActivity extends AppCompatActivity {

    ActivityProductSellBinding binding;
    double TotalPrice =0.0;
    double TotalGST =0.0;
    double ProductOriginalTotal =0.0;

    private  boolean AddShopKeeperFlag = true;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    SellProductDetailsModel sellProductDetailsModel;
    private ArrayList<SellProductDetailsModel> arrayList=new ArrayList<>();
    SellProductDetailsAdapter sellProductDetailsAdapter;

    private ArrayList<String> arrayList_SCode = new ArrayList<>();
    private ArrayList<String> arrayList_SCodeName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductSellBinding.inflate(getLayoutInflater());



        setContentView(binding.getRoot());
        mToolbar = findViewById(R.id.custom_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbarTitle.setText("Sell Product");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.rvproductDetails.setLayoutManager(linearLayoutManager);
        getProducts(GlobalStore.GlobalValue.getUserName());


        binding.btnSearchShopkepper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.txtSearchShopkeeper.getText().toString().isEmpty()){
                    arrayList_SCode.clear();
                    arrayList_SCodeName.clear();
                    arrayList_SCode.add("");
                    arrayList_SCodeName.add("---Select ShoopKeeper---");
                    getShopkeeperID(binding.txtSearchShopkeeper.getText().toString());

                }else{
                    Toast.makeText(ProductSellActivity.this,"Please Enter Value",Toast.LENGTH_LONG).show();
                    binding.txtSearchShopkeeper.requestFocus();
                }

            }
        });

        binding.spActivityGetShopkeeperList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    AddShopKeeperFlag = false;
                    fetchValue(arrayList_SCode.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                AddShopKeeperFlag=true;
            }

        });

        binding.getTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TotalPrice = 0.0;
                TotalGST = 0.0;
                ProductOriginalTotal = 0.0;
                for (int i = 0 ; i<arrayList.size();i++){
                    TotalPrice += arrayList.get(i).getSellingQntyFinalPrice();

                    TotalGST += arrayList.get(i).getGstPrice();
                    ProductOriginalTotal += arrayList.get(i).getSellPrice();
                }
                binding.grandTotal.setText(String.valueOf(TotalPrice));
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TotalPrice==Double.parseDouble(binding.grandTotal.getText().toString()) && binding.grandTotal.getText() != ""){
                    if (!binding.shopkeeperName.getText().toString().isEmpty() &&
                            !binding.shopkeeperAddress.getText().toString().isEmpty() &&
                            !binding.shopkeeperPhone.getText().toString().isEmpty() &&
                            !binding.shopName.getText().toString().isEmpty())
                    {
                        SubmitData(arrayList,TotalPrice);
                        if (AddShopKeeperFlag){
                            SubmitShopKeeper();

                        }

                    }else{
                        Toast.makeText(ProductSellActivity.this,"Please Fill All ShopkeeperDetails",Toast.LENGTH_LONG).show();
                        binding.shopkeeperName.requestFocus();
                        binding.shopkeeperAddress.requestFocus();
                        binding.shopkeeperPhone.requestFocus();
                        binding.shopName.requestFocus();
                    }
                }else{
                    Toast.makeText(ProductSellActivity.this,"Please Click GetAmount First",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void SubmitShopKeeper() {
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_AddShopkeeper(?,?,?,?,?,?)}");
                smt.setString("@Name", binding.shopkeeperName.getText().toString());
                smt.setString("@Phone", binding.shopkeeperPhone.getText().toString());
                smt.setString("@Address", binding.shopkeeperAddress.getText().toString());
                smt.setString("@ShopName", binding.shopName.getText().toString());
                smt.registerOutParameter("@ShopkepperID", java.sql.Types.VARCHAR);
                smt.registerOutParameter("@isError", java.sql.Types.INTEGER);
                smt.executeUpdate();
                String shopkeeperID = smt.getString("@ShopkepperID");
                int isError = smt.getInt("@isError");
                if (isError == 0) {
                    Toast.makeText(this, "Generated Shopkeeper ID", Toast.LENGTH_SHORT).show();
                    Log.d("ShopkeeperID", "Generated Shopkeeper ID: " + shopkeeperID);
                }else{
                    Toast.makeText(this, "New Shopkeeper Add Failed", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.e("Exception1",""+ex);
        }



    }

    private void fetchValue(String phoneNo) {
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetShopkeeperDetails(?)}");
                smt.setString("@PhoneNo",phoneNo);
                smt.execute();
                ResultSet rs = smt.getResultSet();
                if (rs.isBeforeFirst()){
                    while (rs.next()) {
                        binding.shopkeeperName.setText(rs.getString("Name"));
                        binding.shopkeeperPhone.setText(rs.getString("Phone"));
                        binding.shopkeeperAddress.setText(rs.getString("Address"));
                        binding.shopName.setText(rs.getString("ShopName"));

                    }
                }else{
                    Toast.makeText(ProductSellActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            }else{
                Log.d("bbc", "getShopkeeperDetails: Error ");
            }
        } catch (Exception e) {

            Log.d("bbc", "getShopkeeperDetails: "+e);
        }

    }

    private void getShopkeeperID(String Value) {
        final ProgressDialog progressDialog = new ProgressDialog(ProductSellActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetShopkeeper(?)}");
                smt.setString("@SearchValue",Value);
                smt.execute();
                ResultSet rs = smt.getResultSet();
                if (rs.isBeforeFirst()){
                    while (rs.next()) {
                        arrayList_SCode.add(rs.getString("Phone"));
                        arrayList_SCodeName.add(rs.getString("Name")+"-"+rs.getString("Phone"));
                    }
                    binding.spActivityGetShopkeeperList.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter(ProductSellActivity.this,R.layout.spinner_hint, arrayList_SCodeName);
                    binding.spActivityGetShopkeeperList.setAdapter(arrayAdapter);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(ProductSellActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            }else{
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.d("bbc", "getShopkeeperDetails: "+e);
        }


    }

    private void SubmitData(ArrayList<SellProductDetailsModel> arrayList, double totalPrice) {
        String collectionList = "";
        for (int i = 0 ; i<arrayList.size();i++){
            if (arrayList.get(i).getSellingQnty()>0){
                collectionList +=
                        arrayList.get(i).getProductID()+","+arrayList.get(i).getProductName() + "," + arrayList.get(i).getSellingQnty()
                                + "," + arrayList.get(i).getSellingQntyFinalPrice()+"," +arrayList.get(i).getBatchNo()+"," +arrayList.get(i).getExpiryDate()+"," +arrayList.get(i).getMRP()
                                +";";
                Log.d("ergergerg" +
                        "", collectionList);
            }

        }
        final ProgressDialog progressDialog = new ProgressDialog(ProductSellActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call USP_ADROID_INSERT_SELLING_PRO_TEMP_OLD(?,?,?,?,?,?,?,?,?,?)}");
                smt.setString("@UserName",GlobalStore.GlobalValue.getUserName());
                smt.setString("@CollectionList",collectionList);
                smt.setString("@CustomerName",binding.shopkeeperName.getText().toString());
                smt.setString("@CustomerPhn",binding.shopkeeperPhone.getText().toString());
                smt.setString("@CustomerAddrs",binding.shopkeeperAddress.getText().toString());
                smt.setString("@TotalAmount",String.valueOf(totalPrice));

                smt.setString("@GSTAmount",String.valueOf(TotalGST));

                smt.registerOutParameter("@ReturnVoucherNo",java.sql.Types.VARCHAR);
                smt.registerOutParameter("@SaleID",java.sql.Types.VARCHAR);
                smt.registerOutParameter("@ErrorCode",java.sql.Types.INTEGER);
                smt.executeUpdate();
                int ReturnERRORCode  = smt.getInt("@ErrorCode");
                if (ReturnERRORCode==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductSellActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Successful");
                    builder.setMessage("Product Sale Successfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(ProductSellActivity.this, ProductSellActivity.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                        }
                    }).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductSellActivity.this);
                    builder.setCancelable(false);.
                    builder.setTitle("Unable to enter Data");
                    builder.setMessage("Product Sale UnSuccessfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(ProductSellActivity.this, ProductSellActivity.class);
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
                CallableStatement smt = cn.prepareCall("{call ADROID_GetSellItems(?)}");
                smt.setString("@UserName",userName);
                smt.execute();
                ResultSet rs = smt.getResultSet();

                while (rs.next()) {
                    if (rs.getInt("RemianingQnty") >0){
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

                        sellProductDetailsModel.setIsNew(rs.getString("NewProduct"));
                        arrayList.add(sellProductDetailsModel);
                    }

                }
                sellProductDetailsAdapter = new SellProductDetailsAdapter( arrayList,ProductSellActivity.this);
                binding.rvproductDetails.setAdapter(sellProductDetailsAdapter);

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
                startActivity(new Intent(ProductSellActivity.this, MainActivity.class));
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
        startActivity(new Intent(ProductSellActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}