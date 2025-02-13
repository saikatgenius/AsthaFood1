package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.AdapterItemCategory;
import com.example.asthafood.adapters.SellProductDetailsAdapter;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.databinding.ActivityProductSellBinding;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.ItemCategory;
import com.example.asthafood.mssql.models.SellProductDetailsModel;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ProductSellActivity extends AppCompatActivity {

    ActivityProductSellBinding binding;
    double TotalPrice =0.0;

    SellProductDetailsModel sellProductDetailsModel;
    private ArrayList<SellProductDetailsModel> arrayList=new ArrayList<>();
    SellProductDetailsAdapter sellProductDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductSellBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.rvproductDetails.setLayoutManager(linearLayoutManager);
        getProducts(GlobalStore.GlobalValue.getUserName());


        binding.getTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TotalPrice = 0.0;
                for (int i = 0 ; i<arrayList.size();i++){
                    TotalPrice += arrayList.get(i).getSellingQntyFinalPrice();
                }
                binding.price.setText(String.valueOf(TotalPrice));
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TotalPrice==Integer.parseInt(binding.price.getText().toString())){
                    if (binding.price.getText() != ""){
                        SubmitData(arrayList);
                    }else{
                        Toast.makeText(ProductSellActivity.this,"Please Fill All ShopkeeperDetails",Toast.LENGTH_LONG).show();
                        binding.shopkeeperName.requestFocus();
                        binding.shopkeeperAddress.requestFocus();
                        binding.shopkeeperPhone.requestFocus();
                    }
                }else{
                    Toast.makeText(ProductSellActivity.this,"Please Click GetAmount First",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void SubmitData(ArrayList<SellProductDetailsModel> arrayList) {
        String collectionList = "";
        for (int i = 0 ; i<arrayList.size();i++){
            if (arrayList.get(i).getSellingQnty()>0){
                collectionList +=
                        arrayList.get(i).getProductID()+","+arrayList.get(i).getProductName() + "," + arrayList.get(i).getSellingQnty()
                                + "," + arrayList.get(i).getSellingQntyFinalPrice()+"," +arrayList.get(i).getBatchNo()+";";
                Log.d("list", collectionList);
            }

        }
        final ProgressDialog progressDialog = new ProgressDialog(ProductSellActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call USP_ADROID_INSERT_SELLING_PRO_TEMP(?,?,?,?,?,?)}");
                smt.setString("@UserName",GlobalStore.GlobalValue.getUserName());
                smt.setString("@CollectionList",collectionList);
                smt.setString("@CustomerName",GlobalStore.GlobalValue.getUserName());
                smt.setString("@CustomerPhn",GlobalStore.GlobalValue.getUserName());
                smt.setString("@CustomerAddrs",GlobalStore.GlobalValue.getUserName());
                smt.registerOutParameter("@ErrorCode",10);
                smt.execute();


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
                    sellProductDetailsModel = new SellProductDetailsModel();
                    sellProductDetailsModel.setProductID(rs.getString("ProductID"));
                    sellProductDetailsModel.setBatchNo(rs.getString("BatchNo"));
                    sellProductDetailsModel.setAssingQnty(rs.getString("AssingQnty"));
                    sellProductDetailsModel.setSellQunty(rs.getString("SellQnty"));
                    sellProductDetailsModel.setReturnQunt(rs.getString("ReturnQnty"));
                    sellProductDetailsModel.setPrice(rs.getString("EmployeeSellPrice"));
                    sellProductDetailsModel.setRemainingQuntity(rs.getInt("RemianingQnty"));
                    sellProductDetailsModel.setProductName(rs.getString("ProductName"));

                    arrayList.add(sellProductDetailsModel);
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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProductSellActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}