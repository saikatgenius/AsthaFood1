package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.PreviewItemAdapter;
import com.example.asthafood.adapters.SellProductDetailsAdapter;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.databinding.ActivityDamageProductBinding;
import com.example.asthafood.databinding.ActivityProductSellBinding;
import com.example.asthafood.databinding.PreviewBottomSheetBinding;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.SellProductDetailsModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DamageProductlActivity extends AppCompatActivity {

    ActivityDamageProductBinding binding;
    int TotalPrice =0;
    double TotalGST =0.0;
    double ProductOriginalTotal =0.0;
    String BillNo = "";

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
        binding = ActivityDamageProductBinding.inflate(getLayoutInflater());



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






        binding.getTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TotalPrice = 0;
                TotalGST = 0.0;
                ProductOriginalTotal = 0.0;
                for (int i = 0 ; i<arrayList.size();i++){
                    TotalPrice += arrayList.get(i).getSellingQnty();

                    TotalGST += arrayList.get(i).getGstPrice();
                    ProductOriginalTotal += arrayList.get(i).getSellPrice();
                }
                binding.grandTotal.setText(String.valueOf(TotalPrice));
                binding.submit.setVisibility(View.VISIBLE);
                binding.Preview.setVisibility(View.VISIBLE);
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitData(arrayList);

            }
        });



        binding.Preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewItemAdapter previewItemAdapter;
                ArrayList<SellProductDetailsModel> arrayListNew=new ArrayList<>();

                BottomSheetDialog dialog =new  BottomSheetDialog(DamageProductlActivity.this);
                PreviewBottomSheetBinding binding = PreviewBottomSheetBinding.inflate(LayoutInflater.from(DamageProductlActivity.this));
                dialog.setContentView(binding.getRoot());

                LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(DamageProductlActivity.this);
                binding.rvproductDetails.setLayoutManager(linearLayoutManager1);

                for (int i = 0 ; i<arrayList.size();i++){
                    if (arrayList.get(i).getSellingQnty()>0){
                        arrayListNew.add(arrayList.get(i));
                    }
                }

                previewItemAdapter = new PreviewItemAdapter(arrayListNew, DamageProductlActivity.this);
                binding.rvproductDetails.setAdapter(previewItemAdapter);




                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();


            }
        });

    }







    private void SubmitData(ArrayList<SellProductDetailsModel> arrayList) {
        String collectionList = "";
        for (int i = 0 ; i<arrayList.size();i++){
            if (arrayList.get(i).getSellingQnty()>0){
                collectionList +=
                        arrayList.get(i).getProductID()+"," + arrayList.get(i).getSellingQnty()+ "," +arrayList.get(i).getBatchNo()
                                +";";
                Log.d("ergergerg" + "", collectionList);
            }

        }
        final ProgressDialog progressDialog = new ProgressDialog(DamageProductlActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_INSERT_DAMAGE_PRO(?,?,?)}");
                smt.setString("@UserName",GlobalStore.GlobalValue.getUserName());
                smt.setString("@CollectionList",collectionList);
                smt.registerOutParameter("@ErrorCode",java.sql.Types.INTEGER);
                smt.executeUpdate();
                int ReturnERRORCode  = smt.getInt("@ErrorCode");
                if (ReturnERRORCode==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(DamageProductlActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Successful");
                    builder.setMessage("Damage Product Request Submit Successfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                            Intent i = new Intent(DamageProductlActivity.this, DamageProductlActivity.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            startActivity(i);
                            finish();


                        }
                    }).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(DamageProductlActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Unable to enter Data");
                    builder.setMessage("Damage Product Request Submit UnSuccessfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(DamageProductlActivity.this, DamageProductlActivity.class);
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
                sellProductDetailsAdapter = new SellProductDetailsAdapter( arrayList, DamageProductlActivity.this);
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
                startActivity(new Intent(DamageProductlActivity.this, MainActivity.class));
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
        startActivity(new Intent(DamageProductlActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}