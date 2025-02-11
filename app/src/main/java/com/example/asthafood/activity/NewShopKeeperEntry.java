package com.example.asthafood.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asthafood.BuildConfig;
import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.databinding.ActivityNewShopKeeperEntryBinding;
import com.example.asthafood.mssql.SqlManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;

public class NewShopKeeperEntry extends AppCompatActivity {

    ActivityNewShopKeeperEntryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewShopKeeperEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.shopkeeperSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!binding.shopkeeperName.getText().toString().isEmpty()) {
                    if (!binding.shopName.getText().toString().isEmpty()) {
                        if (!binding.shopkeeperAddress.getText().toString().isEmpty()) {
                            if (!binding.shopkeeperPhone.getText().toString().isEmpty()) {
                                submitShopDetails(
                                        binding.shopkeeperName.getText().toString(),
                                        binding.shopName.getText().toString(),
                                        binding.shopkeeperAddress.getText().toString(),
                                        binding.shopkeeperPhone.getText().toString()
                                );
                            }else{
                                binding.shopkeeperPhone.setError("Please enter shopkeeper phone number");
                                binding.shopkeeperPhone.requestFocus();
                            }
                        }else{
                                binding.shopkeeperAddress.setError("Please enter shopkeeper address");
                                binding.shopkeeperAddress.requestFocus();
                        }
                    }else{
                        binding.shopName.setError("Please enter shop name");
                        binding.shopName.requestFocus();
                    }
                }else{
                    binding.shopkeeperName.setError("Please enter shopkeeper name");
                    binding.shopkeeperName.requestFocus();
                }
            }
        });


    }

    public  void submitShopDetails(String shopkeeperName,
                                   String shopName,
                                   String shopkeeperAddress,
                                   String shopkeeperPhone
    ){
        final ProgressDialog progressDialog = new ProgressDialog(NewShopKeeperEntry.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_CheckIfUpdateAvailable(?,?,?,?,?)}");
                smt.setString("@ShopkeeperName",shopkeeperName);
                smt.setString("@ShopName",shopName);
                smt.setString("@ShopkeeperAddress", shopkeeperAddress);
                smt.setString("@ShopkeeperPhone", shopkeeperPhone);
                smt.registerOutParameter("@ShopkeeperCode",java.sql.Types.VARCHAR);
                smt.registerOutParameter("@IsError", Types.INTEGER);
                smt.executeUpdate();
                if (smt.getInt("@IsError") == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewShopKeeperEntry.this);
//                                                TempDataBean.NewMemberErrorCode++;
                    builder.setCancelable(false);
                    builder.setTitle("Successful");
                    builder.setMessage("Shopkeeper Saved Successfully.\n\nThe temporary Shopkeeper Code is " + smt.getString("@ShopkeeperCode"));

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(NewShopKeeperEntry.this, NewShopKeeperEntry.class);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            startActivity(i);
                            finish();
                            progressDialog.dismiss();
                        }
                    }).show();


                }else{
                    if (smt.getInt("@IsError") == 1) {
                        Toast.makeText(NewShopKeeperEntry.this, "Shopkeeper Already exist", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Save Failed", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
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
        //super.onBackPressed();
        startActivity(new Intent(NewShopKeeperEntry.this, MainActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}