package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.asthafood.Util.GlobalReqProductList;
import com.example.asthafood.Util.ReqProductList;
import com.example.asthafood.adapters.CategoryItemsAdapter;
import com.example.asthafood.adapters.RequestCartAdapter;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.databinding.ActivityRequestCartItemBinding;
import com.example.asthafood.mssql.SqlManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;

public class RequestCartItemActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    ActivityRequestCartItemBinding binding;

    RequestCartAdapter requestCartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestCartItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        binding.rvItemcategory.setLayoutManager(linearLayoutManager);

        requestCartAdapter = new RequestCartAdapter(GlobalReqProductList.ReqData,RequestCartItemActivity.this);
        binding.rvItemcategory.setAdapter(requestCartAdapter);


        mToolbar = findViewById(R.id.custom_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolbarTitle.setText("Request Cart");

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalReqProductList.ReqData.size()>0){
                    SubmitRequest(GlobalReqProductList.ReqData);
                }else{
                    Toast.makeText(RequestCartItemActivity.this, "No Request In Queue , Please Add Some Product", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RequestCartItemActivity.this, RequestProductSubmitActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



    }

    private void SubmitRequest(ArrayList<ReqProductList> reqData) {

        String collectionList = "";
        for (int i = 0 ; i<reqData.size();i++){
            collectionList +=
                    reqData.get(i).getId()+","+reqData.get(i).getName() + "," + reqData.get(i).getQunt()+";";
                Log.d("ergergerg" + "", collectionList);
        }

        final ProgressDialog progressDialog = new ProgressDialog(RequestCartItemActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_Request_Product(?,?,?,?)}");
                smt.setString("@UserName", GlobalStore.GlobalValue.getUserName());
                smt.setString("@CollectionList",collectionList);
                smt.registerOutParameter("@ReturnVoucherNo",java.sql.Types.VARCHAR);
                smt.registerOutParameter("@isError",java.sql.Types.INTEGER);
                smt.executeUpdate();
                int ReturnERRORCode  = smt.getInt("@isError");
                if (ReturnERRORCode==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestCartItemActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Successful");
                    builder.setMessage("Product Request Successfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(RequestCartItemActivity.this, MainActivity.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                            GlobalReqProductList.ReqData.clear();
                        }
                    }).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RequestCartItemActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Unable to enter Data");
                    builder.setMessage("Product Sale UnSuccessfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(RequestCartItemActivity.this, RequestCartItemActivity.class);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RequestCartItemActivity.this, RequestProductSubmitActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}