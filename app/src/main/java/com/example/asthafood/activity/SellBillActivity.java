package com.example.asthafood.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.AdapterBillReport;
import com.example.asthafood.adapters.AdapterSellReport;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.SetGetBillReport;
import com.example.asthafood.mssql.models.SetGetSellReport;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

public class SellBillActivity extends AppCompatActivity implements View.OnClickListener {
     // toolbar
     private Toolbar mToolbar;
     private TextView mToolbarTitle,mTV_buyerNameTextView;

     private TextView mTv_fDate;

     private  EditText Edt_txtSearchShopkeeper;
     private TextView mTv_tDate;
     private EditText mEt_loanCode;
     private AppCompatButton mBtn_show;

     private ProgressBar mPb_proggress;

     private EditText mEt_enterOfficeId;
     private Button mBtn_showAll,btn_SearchShopkepper;

     // vars
     private int fdate = 0;
     private int tdate = 0;
     int mYear, mMonth, mDay;
     private int currentDay, currentMonth, currentYear;
     private Calendar mCalendar;
     private  Double totalAmount=0.0;

     private Connection cn;
     private Spinner activity_get_shopkeeper_list;

     private ArrayList<SetGetBillReport> mArrayListSellReport;
     private RecyclerView mRv_loanDueReport;
     private AdapterBillReport adapterSellReport;


    private ArrayList<String> arrayList_SCode = new ArrayList<>();
    private ArrayList<String> arrayList_SCodeName = new ArrayList<>();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_bill_report);

         setViewReferences();
         bindEventHandlers();

         setSupportActionBar(mToolbar);
         if (getSupportActionBar() != null) {
             getSupportActionBar().setDisplayShowTitleEnabled(false);
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
             getSupportActionBar().setDisplayShowHomeEnabled(true);
         }
         mToolbarTitle.setText("Bill Report DayWise");

         mArrayListSellReport = new ArrayList<>();
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
         mRv_loanDueReport.setLayoutManager(linearLayoutManager);
         mArrayListSellReport.clear();
       //  getLoanDueReport(1, 2, GlobalStore.GlobalValue.getUserName());



     }

     private void setViewReferences() {
         mToolbar = findViewById(R.id.custom_toolbar);
         mToolbarTitle = findViewById(R.id.toolbar_title);

         mTv_fDate = findViewById(R.id.tv_activity_agent_loan_due_report_fdate);
         mTv_tDate = findViewById(R.id.tv_activity_agent_loan_due_report_tdate);
         mEt_loanCode = findViewById(R.id.ev_activity_agent_loan_due_report_enter_loan_code);
         mBtn_show = findViewById(R.id.btn_activity_agent_loan_due_report_show);
         mRv_loanDueReport = findViewById(R.id.rv_activity_loan_due_report);

         mPb_proggress = findViewById(R.id.pb_activity_agent_loan_due_report);

         //mEt_enterOfficeId = findViewById(R.id.et_activity_agent_loan_due_report_enter_office_id);
         mBtn_showAll = findViewById(R.id.btn_activity_agent_loan_due_report_show_all);
         mTV_buyerNameTextView=findViewById(R.id.totalamt);
         Edt_txtSearchShopkeeper=findViewById(R.id.txtSearchShopkeeper);
         btn_SearchShopkepper=findViewById(R.id.btnSearchShopkepper);
         activity_get_shopkeeper_list=findViewById(R.id.sp_activity_get_shopkeeper_list);
     }

     private void bindEventHandlers() {
         mTv_tDate.setOnClickListener(this);
         mTv_fDate.setOnClickListener(this);
         mBtn_showAll.setOnClickListener(this);
         btn_SearchShopkepper.setOnClickListener(this);
     }

     @Override
     public void onClick(View v) {
         if (v == mTv_tDate) {

             if (!mTv_fDate.getText().toString().isEmpty()) {
                 selectTDate(fdate);
             }else {
                 Toast.makeText(SellBillActivity.this,"Please Enter From Date",Toast.LENGTH_LONG).show();
                 mTv_fDate.requestFocus();
             }


         } else if (v==btn_SearchShopkepper) {
             if (!Edt_txtSearchShopkeeper.getText().toString().isEmpty()){
                 arrayList_SCode.clear();
                 arrayList_SCodeName.clear();
                 arrayList_SCode.add("");
                 arrayList_SCodeName.add("---Select ShoopKeeper---");
                 getShopkeeperID(Edt_txtSearchShopkeeper.getText().toString());

             }else{
                 Toast.makeText(SellBillActivity.this,"Please Enter Value",Toast.LENGTH_LONG).show();
                 Edt_txtSearchShopkeeper.requestFocus();
             }
         } else if (v==mTv_fDate) {

             selectFDate();
         }
     }

    private void getShopkeeperID(String Value) {
        final ProgressDialog progressDialog = new ProgressDialog(SellBillActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetSellBill_Details(?)}");
                smt.setString("@date",Value);

                smt.execute();
                ResultSet rs = smt.getResultSet();
                if (rs.isBeforeFirst()){
                    while (rs.next()) {
                        arrayList_SCode.add(rs.getString("Phone"));
                        arrayList_SCodeName.add(rs.getString("Name")+"-"+rs.getString("Phone"));
                    }
                    activity_get_shopkeeper_list.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter(SellBillActivity.this,R.layout.spinner_hint, arrayList_SCodeName);
                    activity_get_shopkeeper_list.setAdapter(arrayAdapter);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(SellBillActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            }else{
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.d("bbc", "getShopkeeperDetails: "+e);
        }


    }


    public void getLoanDueReport(int fDate, int tDate, String username) {
         mTV_buyerNameTextView.setText("0");
         mPb_proggress.setVisibility(View.VISIBLE);
         cn = new SqlManager().getSQLConnection();
         mArrayListSellReport.clear();
         SetGetBillReport SetGetBillReport = null;
         totalAmount=0.0;
         try {
             if (cn != null) {
                 CallableStatement smt = cn.prepareCall("{call ADROID_GetSellReport(?)}");
                 smt.setString("@UserValue", username);
                 smt.execute();
                 ResultSet rs = smt.getResultSet();
                 if (rs.isBeforeFirst()) {
                     while (rs.next()) {
                         SetGetBillReport = new SetGetBillReport();
                         SetGetBillReport.setSaleDate(rs.getString("saledate"));
                         SetGetBillReport.setSaleid(rs.getString("SaleID"));
                         SetGetBillReport.setPayableAmt(rs.getString("PayableAmt"));
                         SetGetBillReport.setCoustomerPh(rs.getString("CustomerPhoneNo"));
                         SetGetBillReport.setCoustomerName(rs.getString("CustomerName"));
                         SetGetBillReport.setItemID(rs.getString("ItemID"));
                         SetGetBillReport.setQuantity(rs.getString("Quantity"));
                         SetGetBillReport.setSalePrice(rs.getString("SalePrice"));
                         SetGetBillReport.setItemName(rs.getString("ItemName"));
                         SetGetBillReport.setBatchNo(rs.getString("BatchNo"));
                         totalAmount=totalAmount+Double.parseDouble(rs.getString("PayableAmt"));
                         mArrayListSellReport.add(SetGetBillReport);

                     }

                     adapterSellReport = new AdapterBillReport(SellBillActivity.this, mArrayListSellReport);
                     mRv_loanDueReport.setAdapter(adapterSellReport);
                     mPb_proggress.setVisibility(View.GONE);
                     mTV_buyerNameTextView.setText(String.valueOf(totalAmount));
                     adapterSellReport.notifyDataSetChanged();

                 } else {
                     adapterSellReport.notifyDataSetChanged();
                     mPb_proggress.setVisibility(View.GONE);
                     Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                 }
             } else {
                 mPb_proggress.setVisibility(View.GONE);
                 Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
             }
         } catch (Exception ex) {
             mPb_proggress.setVisibility(View.GONE);
             Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
         }
     }


     private void selectFDate() {
         Calendar c = Calendar.getInstance();
         mYear = c.get(Calendar.YEAR);
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);

         // Calendar
         mCalendar = Calendar.getInstance();
         currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
         currentMonth = mCalendar.get(Calendar.MONTH);
         currentYear = mCalendar.get(Calendar.YEAR);

         DatePickerDialog datePickerDialog = new DatePickerDialog(SellBillActivity.this, new DatePickerDialog.OnDateSetListener() {
             @Override
             public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                 month += 1;

                 mTv_fDate.setText(dayOfMonth + "-" + month + "-" + year);
                 fdate = Integer.parseInt(Integer.toString(year) + String.format("%02d", month) + String.format("%02d", dayOfMonth));

             }
         }, currentYear, currentMonth, currentDay);
         mCalendar.set(currentYear, currentMonth, currentDay);

         datePickerDialog.getDatePicker().setMaxDate(mCalendar.getTimeInMillis());
         datePickerDialog.show();
     }

     private void selectTDate(int fdate) {
         Calendar c = Calendar.getInstance();
         mYear = c.get(Calendar.YEAR);
         mMonth = c.get(Calendar.MONTH);
         mDay = c.get(Calendar.DAY_OF_MONTH);

         // Calendar
         mCalendar = Calendar.getInstance();
         currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
         currentMonth = mCalendar.get(Calendar.MONTH);
         currentYear = mCalendar.get(Calendar.YEAR);

         DatePickerDialog datePickerDialog = new DatePickerDialog(SellBillActivity.this, new DatePickerDialog.OnDateSetListener() {
             @Override
             public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                 month += 1;

                 mTv_tDate.setText(dayOfMonth + "-" + month + "-" + year);
                 tdate = Integer.parseInt(Integer.toString(year) + String.format("%02d", month) + String.format("%02d", dayOfMonth));

                 totalAmount=0.0;
                 mArrayListSellReport.clear();
                 getCollReport( tdate, fdate,GlobalStore.GlobalValue.getUserName());
             }
         }, currentYear, currentMonth, currentDay);
         mCalendar.set(currentYear, currentMonth, currentDay);

         datePickerDialog.getDatePicker().setMaxDate(mCalendar.getTimeInMillis());
         datePickerDialog.show();
     }



    public void getCollReport( int tDate,int fDate, String username) {
        mTV_buyerNameTextView.setText("0");
        mPb_proggress.setVisibility(View.VISIBLE);
        cn = new SqlManager().getSQLConnection();
        mArrayListSellReport.clear();
        SetGetBillReport setGetBillReport = null;

        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetSellBill_Details(?,?,?)}");
                smt.setInt("@fdate", fDate);
                smt.setInt("@tdate", tDate);
                smt.setString("@UserName", GlobalStore.GlobalValue.getUserName());
                smt.execute();
                ResultSet rs = smt.getResultSet();
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        setGetBillReport = new SetGetBillReport();
                        setGetBillReport.setSaleDate(rs.getString("saleDate"));
                        setGetBillReport.setSaleid(rs.getString("SaleID"));
                        setGetBillReport.setPayableAmt(rs.getString("PayableAmt"));
                        setGetBillReport.setCoustomerPh(rs.getString("CustomerPhoneNo"));
                        setGetBillReport.setCoustomerName(rs.getString("CustomerName"));
                     /*   setGetBillReport.setItemID(rs.getString("ItemID"));
                        setGetBillReport.setQuantity(rs.getString("Quantity"));
                        setGetBillReport.setSalePrice(rs.getString("SalePrice"));
                        setGetBillReport.setItemName(rs.getString("ItemName"));
                        setGetBillReport.setBatchNo(rs.getString("BatchNo"));*/
                        totalAmount=totalAmount+Double.parseDouble(rs.getString("PayableAmt"));
                        mArrayListSellReport.add(setGetBillReport);

                    }
                    mTV_buyerNameTextView.setText(String.valueOf(totalAmount));
                    adapterSellReport = new AdapterBillReport(SellBillActivity.this, mArrayListSellReport);
                    mRv_loanDueReport.setAdapter(adapterSellReport);
                    mPb_proggress.setVisibility(View.GONE);
                    adapterSellReport.notifyDataSetChanged();


                } else {
                   // adapterSellReport.notifyDataSetChanged();
                    mPb_proggress.setVisibility(View.GONE);
                    Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
                }
            } else {
                mPb_proggress.setVisibility(View.GONE);
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            mPb_proggress.setVisibility(View.GONE);
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
            Log.e("Exception1", "" + ex);
        }
    }

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:
                 // todo: goto back activity from here
                 startActivity(new Intent(SellBillActivity.this, MainActivity.class));
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
         startActivity(new Intent(SellBillActivity.this, MainActivity.class));
         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         finish();
     }
 }

