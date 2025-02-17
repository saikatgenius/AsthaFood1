package com.example.asthafood.activity;
import static androidx.constraintlayout.widget.ConstraintSet.GONE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;

import com.example.asthafood.adapters.AdapterSellReport;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.mssql.models.SetGetSellReport;
import com.example.asthafood.mssql.SqlManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

   public class SellReportActivity extends AppCompatActivity implements View.OnClickListener {
        // toolbar
        private Toolbar mToolbar;
        private TextView mToolbarTitle,mTV_buyerNameTextView;

        private TextView mTv_fDate;
        private TextView mTv_tDate;
        private EditText mEt_loanCode;
        private AppCompatButton mBtn_show;

        private ProgressBar mPb_proggress;

        private EditText mEt_enterOfficeId;
        private Button mBtn_showAll;

        // vars
        private int fdate = 0;
        private int tdate = 0;
        int mYear, mMonth, mDay;
        private int currentDay, currentMonth, currentYear;
        private Calendar mCalendar;
        private  Double totalAmount=0.0;

        private Connection cn;

        private ArrayList<SetGetSellReport> mArrayListSellReport;
        private RecyclerView mRv_loanDueReport;
        private AdapterSellReport adapterSellReport;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sell_report);

            setViewReferences();
            bindEventHandlers();

            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            mToolbarTitle.setText("Sell Report");

            mArrayListSellReport = new ArrayList<>();
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRv_loanDueReport.setLayoutManager(linearLayoutManager);
            mArrayListSellReport.clear();
            getLoanDueReport(1, 2, GlobalStore.GlobalValue.getUserName());



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
        }

        private void bindEventHandlers() {
            mTv_fDate.setOnClickListener(this);
            mTv_tDate.setOnClickListener(this);
            mBtn_show.setOnClickListener(this);
            mBtn_showAll.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == mTv_fDate) {
                selectFDate();
            } else if (v == mTv_tDate) {
                selectTDate();
            } else if (v == mBtn_show) {
                if (mTv_fDate.getText().toString().trim().length() > 0) {
                    if (mTv_tDate.getText().toString().trim().length() > 0) {
                        mArrayListSellReport.clear();
                        getLoanDueReport(fdate, tdate, GlobalStore.GlobalValue.getUserName());
                    } else {
                        mTv_tDate.performClick();
                        Toast.makeText(this, "Select tDate", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mTv_fDate.performClick();
                    Toast.makeText(this, "Select fDate", Toast.LENGTH_SHORT).show();
                }

            } /*else if (v == mBtn_showAll) {
            if (mTv_fDate.getText().toString().trim().length() > 0) {
                if (mTv_tDate.getText().toString().trim().length() > 0) {
                    mArrayListDueReport.clear();
                    showAllData(fdate, tdate, GlobalStore.GlobalValue.getOfficeID());
                } else {
                    mTv_tDate.performClick();
                    Toast.makeText(this, "Select tDate", Toast.LENGTH_SHORT).show();
                }
            } else {
                mTv_fDate.performClick();
                Toast.makeText(this, "Select fDate", Toast.LENGTH_SHORT).show();
            }
        }*/
        }



        public void getLoanDueReport(int fDate, int tDate, String username) {
            mTV_buyerNameTextView.setText("0");
            mPb_proggress.setVisibility(View.VISIBLE);
            cn = new SqlManager().getSQLConnection();
            mArrayListSellReport.clear();
            SetGetSellReport setGetLoanDueReport = null;

            totalAmount=0.0;
            setGetLoanDueReport = new SetGetSellReport();
         /*   for  (int i = 1; i <= 5; i++) {
                setGetLoanDueReport.setProductName("Pav Bhaji Masala");
                setGetLoanDueReport.setProductDetails("Sunrise Pav Bhaji Masala is a spice blend that captures the authentic flavours of our famous street food, pav bhaji.");
                setGetLoanDueReport.setProductId("45834");
                setGetLoanDueReport.setBillNo("57297");
                setGetLoanDueReport.setDate("2025/10/10");
                setGetLoanDueReport.setAmount("120");
                setGetLoanDueReport.setBuyer("Saikat");
                setGetLoanDueReport.setBuyer("Saikat");
                setGetLoanDueReport.setProductQuantity("12");
                mArrayListSellReport.add(setGetLoanDueReport);
            }





            adapterSellReport = new AdapterSellReport(SellReportActivity.this, mArrayListSellReport);
            mRv_loanDueReport.setAdapter(adapterSellReport);
            mPb_proggress.setVisibility(View.GONE);
*/

            try {
                if (cn != null) {
                    CallableStatement smt = cn.prepareCall("{call ADROID_GetSellReport(?)}");
                    smt.setString("@UserValue", username);

                    // smt.setString("@arrCode", GlobalStore.GlobalValue.getUserName());
                    //smt.setString("@MemberCode", memberCode);
                    smt.execute();
                    ResultSet rs = smt.getResultSet();
                    if (rs.isBeforeFirst()) {
                        while (rs.next()) {
                            setGetLoanDueReport = new SetGetSellReport();
                            setGetLoanDueReport.setSaleDate(rs.getString("saledate"));
                            setGetLoanDueReport.setSaleid(rs.getString("SaleID"));
                            setGetLoanDueReport.setPayableAmt(rs.getString("PayableAmt"));
                            setGetLoanDueReport.setCoustomerPh(rs.getString("CustomerPhoneNo"));
                            setGetLoanDueReport.setCoustomerName(rs.getString("CustomerName"));
                            setGetLoanDueReport.setItemID(rs.getString("ItemID"));
                            setGetLoanDueReport.setQuantity(rs.getString("Quantity"));
                            setGetLoanDueReport.setSalePrice(rs.getString("SalePrice"));
                            setGetLoanDueReport.setItemName(rs.getString("ItemName"));
                            setGetLoanDueReport.setBatchNo(rs.getString("BatchNo"));
                            totalAmount=totalAmount+Double.parseDouble(rs.getString("PayableAmt"));
                            mArrayListSellReport.add(setGetLoanDueReport);

                        }

                        adapterSellReport = new AdapterSellReport(SellReportActivity.this, mArrayListSellReport);
                        mRv_loanDueReport.setAdapter(adapterSellReport);
                        mPb_proggress.setVisibility(View.GONE);
                        mTV_buyerNameTextView.setText(String.valueOf(totalAmount));
                        adapterSellReport.notifyDataSetChanged();

                    } else {
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(SellReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        private void selectTDate() {
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            // Calendar
            mCalendar = Calendar.getInstance();
            currentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
            currentMonth = mCalendar.get(Calendar.MONTH);
            currentYear = mCalendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SellReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month += 1;

                    mTv_tDate.setText(dayOfMonth + "-" + month + "-" + year);
                    tdate = Integer.parseInt(Integer.toString(year) + String.format("%02d", month) + String.format("%02d", dayOfMonth));

                    totalAmount=0.0;
                    mArrayListSellReport.clear();
                    getCollReport( tdate, GlobalStore.GlobalValue.getUserName());
                }
            }, currentYear, currentMonth, currentDay);
            mCalendar.set(currentYear, currentMonth, currentDay);

            datePickerDialog.getDatePicker().setMaxDate(mCalendar.getTimeInMillis());
            datePickerDialog.show();
        }



       public void getCollReport( int tDate, String username) {
           mTV_buyerNameTextView.setText("0");
           mPb_proggress.setVisibility(View.VISIBLE);
           cn = new SqlManager().getSQLConnection();
           mArrayListSellReport.clear();
           SetGetSellReport setGetLoanDueReport = null;


           setGetLoanDueReport = new SetGetSellReport();
         /*   for  (int i = 1; i <= 5; i++) {
                setGetLoanDueReport.setProductName("Pav Bhaji Masala");
                setGetLoanDueReport.setProductDetails("Sunrise Pav Bhaji Masala is a spice blend that captures the authentic flavours of our famous street food, pav bhaji.");
                setGetLoanDueReport.setProductId("45834");
                setGetLoanDueReport.setBillNo("57297");
                setGetLoanDueReport.setDate("2025/10/10");
                setGetLoanDueReport.setAmount("120");
                setGetLoanDueReport.setBuyer("Saikat");
                setGetLoanDueReport.setBuyer("Saikat");
                setGetLoanDueReport.setProductQuantity("12");
                mArrayListSellReport.add(setGetLoanDueReport);
            }





            adapterSellReport = new AdapterSellReport(SellReportActivity.this, mArrayListSellReport);
            mRv_loanDueReport.setAdapter(adapterSellReport);
            mPb_proggress.setVisibility(View.GONE);
*/

           try {
               if (cn != null) {
                   CallableStatement smt = cn.prepareCall("{call ADROID_GetSellRepor_datewiset(?,?)}");
                   smt.setString("@UserValue", username);
                   smt.setInt("@date", tDate);

                   // smt.setString("@arrCode", GlobalStore.GlobalValue.getUserName());
                   //smt.setString("@MemberCode", memberCode);
                   smt.execute();
                   ResultSet rs = smt.getResultSet();
                   if (rs.isBeforeFirst()) {
                       while (rs.next()) {
                           setGetLoanDueReport = new SetGetSellReport();
                           setGetLoanDueReport.setSaleDate(rs.getString("saledate"));
                           setGetLoanDueReport.setSaleid(rs.getString("SaleID"));
                           setGetLoanDueReport.setPayableAmt(rs.getString("PayableAmt"));
                           setGetLoanDueReport.setCoustomerPh(rs.getString("CustomerPhoneNo"));
                           setGetLoanDueReport.setCoustomerName(rs.getString("CustomerName"));
                           setGetLoanDueReport.setItemID(rs.getString("ItemID"));
                           setGetLoanDueReport.setQuantity(rs.getString("Quantity"));
                           setGetLoanDueReport.setSalePrice(rs.getString("SalePrice"));
                           setGetLoanDueReport.setItemName(rs.getString("ItemName"));
                           setGetLoanDueReport.setBatchNo(rs.getString("BatchNo"));
                           totalAmount=totalAmount+Double.parseDouble(rs.getString("PayableAmt"));
                           mArrayListSellReport.add(setGetLoanDueReport);
                           mTV_buyerNameTextView.setText(String.valueOf(totalAmount));
                       }

                       adapterSellReport = new AdapterSellReport(SellReportActivity.this, mArrayListSellReport);
                       mRv_loanDueReport.setAdapter(adapterSellReport);
                       mPb_proggress.setVisibility(View.GONE);
                       adapterSellReport.notifyDataSetChanged();

                   } else {
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

       @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // todo: goto back activity from here
                    startActivity(new Intent(SellReportActivity.this, MainActivity.class));
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
            startActivity(new Intent(SellReportActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

