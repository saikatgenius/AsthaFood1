package com.example.asthafood.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asthafood.R;
import com.example.asthafood.bean.AppData;
import com.example.asthafood.dl.LoginManagement;
import com.example.asthafood.mssql.SqlManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Test extends AppCompatActivity {


    private EditText SearchLoan;
    private Button btnShopkepper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);

        SearchLoan = findViewById(R.id.txtSearchLoan);
        btnShopkepper = findViewById(R.id.btnSearchShopkepper);


        btnShopkepper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchLoan.getText().toString().trim();


            }
        });

        AddShopkepper();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private void AddShopkepper() {
        Connection cn = new SqlManager().getSQLConnection();
        String ut = "";
        AppData ad;
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_AddShopkeeper(?,?,?,?,?)}");
                smt.setString("@Name", "membqCoe");
                smt.setString("@Phone", "12qw1df234");
                smt.setString("@Address", "mewqmb12er3Ce");
                smt.registerOutParameter("@ShopkepperID", java.sql.Types.VARCHAR);
                smt.registerOutParameter("@isError", java.sql.Types.INTEGER);
                smt.executeUpdate();
                String shopkeeperID = smt.getString("@ShopkepperID");
                int isError = smt.getInt("@isError");
                if (isError == 0) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    Log.d("ShopkeeperID", "Generated Shopkeeper ID: " + shopkeeperID);

                    AlertDialog.Builder builder = new AlertDialog.Builder(Test.this);
                    builder.setCancelable(false);
                    builder.setTitle("Successful");
                    builder.setMessage("New Shopkeeper Saved Successfully.\n\nShopkeeper Temporary Code is " + shopkeeperID);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);


                        }
                    }).show();


                }else{
                    Toast.makeText(this, "New Shopkeeper Add Faild", Toast.LENGTH_SHORT).show();
                }


               // ResultSet rs = smt.getResultSet();
              /*  if (rs.getInt("isError") == 0) {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "New Shopkeeper Add Faild", Toast.LENGTH_SHORT).show();
                }*/


            }
        } catch (Exception ex) {
          Log.e("Exception1",""+ex);
        }

    }

}