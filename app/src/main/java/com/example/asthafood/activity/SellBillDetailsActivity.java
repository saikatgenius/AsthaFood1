package com.example.asthafood.activity;

import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.AdapterBillDetailsReport;
import com.example.asthafood.adapters.AdapterBillReport;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.SellProductDetailsModel;
import com.example.asthafood.mssql.models.SetGetBillReport;
import com.example.asthafood.mssql.models.SetGetSellDetailsReport;
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
import java.util.Calendar;

public class SellBillDetailsActivity extends AppCompatActivity implements View.OnClickListener {
     // toolbar
     private Toolbar mToolbar;
   // private ArrayList<SellProductDetailsModel> arrayList=new ArrayList<>();
     private TextView mToolbarTitle,mTV_buyerNameTextView;
    SellProductDetailsModel sellProductDetailsModel;
     private TextView mTv_fDate;
     private  AppCompatButton Btn_download;

     private  EditText Edt_txtSearchShopkeeper;
     private TextView mTv_tDate;
     private  String saleid="";
     private  String BuyerName="";
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

     private ArrayList<SetGetSellDetailsReport> mArrayListSellReport;
     private RecyclerView mRv_loanDueReport;
     private AdapterBillDetailsReport adapterSellReport;


    private ArrayList<String> arrayList_SCode = new ArrayList<>();
    private ArrayList<String> arrayList_SCodeName = new ArrayList<>();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_bill_details);

         setViewReferences();
         bindEventHandlers();

         setSupportActionBar(mToolbar);
         if (getSupportActionBar() != null) {
             getSupportActionBar().setDisplayShowTitleEnabled(false);
             getSupportActionBar().setDisplayHomeAsUpEnabled(true);
             getSupportActionBar().setDisplayShowHomeEnabled(true);
         }
         mToolbarTitle.setText("Bill Report");

         mArrayListSellReport = new ArrayList<>();
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
         mRv_loanDueReport.setLayoutManager(linearLayoutManager);
         mArrayListSellReport.clear();
       //  getLoanDueReport(1, 2, GlobalStore.GlobalValue.getUserName());

         Intent intent=getIntent();
          saleid=intent.getStringExtra("Saleid");
         BuyerName=intent.getStringExtra("CoustomerName");




         getCollReport(1,saleid);

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
         Btn_download=findViewById(R.id.download);
     }

     private void bindEventHandlers() {
         mTv_tDate.setOnClickListener(this);
         mBtn_showAll.setOnClickListener(this);
         btn_SearchShopkepper.setOnClickListener(this);
         Btn_download.setOnClickListener(this);
     }

     @Override
     public void onClick(View v) {
         if (v == mTv_tDate) {
             selectTDate();
         } else if (v==btn_SearchShopkepper) {
             if (!Edt_txtSearchShopkeeper.getText().toString().isEmpty()){
                 arrayList_SCode.clear();
                 arrayList_SCodeName.clear();
                 arrayList_SCode.add("");
                 arrayList_SCodeName.add("---Select ShoopKeeper---");
                 getShopkeeperID(Edt_txtSearchShopkeeper.getText().toString());

             }else{
                 Toast.makeText(SellBillDetailsActivity.this,"Please Enter Value",Toast.LENGTH_LONG).show();
                 Edt_txtSearchShopkeeper.requestFocus();
             }
         } else if (v == Btn_download) {


             downloadSavingStatement(mArrayListSellReport);

         }
     }



    private void downloadSavingStatement(ArrayList<SetGetSellDetailsReport> arrayList) {

        String billno="";
        Log.e("arrayList1",""+ arrayList.get(0).getProductId());
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        document.setPageSize(new Rectangle(850, 890));
        long time = System.currentTimeMillis();

        try {

            String fileName = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + ("BILL" + "_" + time + ".pdf");// + ".pdf";
            } else {
                fileName = Environment.getExternalStorageDirectory().toString() + "/" + ("BILL" + "_" + time + ".pdf");//"test" + ".pdf";
            }


            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            Font font = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph headingPara = new Paragraph(getString(R.string.app_name), font);
            headingPara.setAlignment(Element.ALIGN_CENTER);
            headingPara.setSpacingAfter(20f);

            document.open();

            com.itextpdf.text.pdf.PdfPTable containerTable1 = new com.itextpdf.text.pdf.PdfPTable(1);
            containerTable1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            containerTable1.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Image img = Image.getInstance(byteArray);
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleAbsolute(100, 70); // Adjust image size


            Font hf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            hf.setStyle(Font.UNDERLINE);

            Font fontMiniStatement2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            fontMiniStatement2.setStyle(Font.UNDERLINE);
            Font fontAccountNo3 = FontFactory.getFont(FontFactory.HELVETICA, 14);
            Font fontBold14 = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font fontBoldt_head = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);


            com.itextpdf.text.pdf.PdfPTable leftRows = new com.itextpdf.text.pdf.PdfPTable(3);
            leftRows.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            leftRows.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            com.itextpdf.text.pdf.PdfPTable rightRows = new com.itextpdf.text.pdf.PdfPTable(3);
            rightRows.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
            rightRows.getDefaultCell().setBorder(Rectangle.NO_BORDER);


            PdfPCell c1 = new PdfPCell(new PdfPCell(new Paragraph("BILL \n\n", hf)));
            c1.setColspan(3);
            c1.setBorder(Rectangle.NO_BORDER);
            leftRows.addCell(c1);
            leftRows.addCell(new Paragraph("SELLER ID ", fontBold14));
            leftRows.addCell(":");
            leftRows.addCell("" + GlobalStore.GlobalValue.getUserName() + "\t\t\n\n");
            leftRows.addCell(new Paragraph("BUYER NAME ", fontBold14));
            leftRows.addCell(":");
            leftRows.addCell("" + BuyerName + "\t\t\n\n");

            leftRows.addCell(new Paragraph("SELLER NAME ", fontBold14));
            leftRows.addCell(":");
            leftRows.addCell("" + GlobalStore.GlobalValue.getUserOriginalName() + "\t\t\n\n");
            leftRows.addCell(new Paragraph("Bill No", fontBold14));
            leftRows.addCell(":");
            leftRows.addCell("" + saleid + "\t\t\n\n");

            com.itextpdf.text.pdf.PdfPTable totalRows = new com.itextpdf.text.pdf.PdfPTable(7);
            totalRows.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            totalRows.addCell(new Paragraph("ProductID", fontBoldt_head));
            totalRows.addCell(new Paragraph("ProductName", fontBoldt_head));
            totalRows.addCell(new Paragraph("SellingQnty", fontBoldt_head));
            totalRows.addCell(new Paragraph("Price", fontBoldt_head));
            totalRows.addCell(new Paragraph("BatchNo", fontBoldt_head));
            totalRows.addCell(new Paragraph("ExpiryDate", fontBoldt_head));
            totalRows.addCell(new Paragraph("MRP", fontBoldt_head));

            Double balance=0.0;

            for (int i = 0 ; i<arrayList.size();i++){
               // if (arrayList.get(i).getSellingQnty()>0){
                  //  Log.e("arrayList51",""+ arrayList.get(i).getProductID());
                    document.open();
                  //  Log.e("arrayList21",""+ arrayList.get(i).getProductID());

                    balance +=  Double.valueOf(arrayList.get(i).getPayableAmt());

                    totalRows.addCell("" + arrayList.get(i).getItemID());
                    totalRows.addCell("" + arrayList.get(i).getItemName());
                    totalRows.addCell("" + arrayList.get(i).getCoustomerName());
                    totalRows.addCell("" + arrayList.get(i).getPayableAmt());
                    totalRows.addCell("" + arrayList.get(i).getCoustomerPh());
                    totalRows.addCell("" + arrayList.get(i).getExpary());
                    totalRows.addCell("" + arrayList.get(i).getBatchNo());


                //}

            }
            rightRows.addCell("");
            rightRows.addCell("");
            PdfPCell imgcell = new PdfPCell(img);
            imgcell.setColspan(2);
            imgcell.setBorder(Rectangle.NO_BORDER);
            rightRows.addCell(imgcell);
            rightRows.addCell("");
            rightRows.addCell("");
            rightRows.addCell("");
            com.itextpdf.text.pdf.PdfPTable tableRows = new com.itextpdf.text.pdf.PdfPTable(6);
            tableRows.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            tableRows.setWidthPercentage(100f);

            tableRows.setHeaderRows(1);
            double depobalance = 0.0;
            double withdrawbalance = 0.0;
            int indx = 1;

            com.itextpdf.text.pdf.PdfPTable containerTable = new com.itextpdf.text.pdf.PdfPTable(2);
            containerTable.setWidthPercentage(100);
            containerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            containerTable.addCell(leftRows);
            containerTable.addCell(rightRows);
            document.add(containerTable);
            document.add(new Paragraph("\n"));
            document.add(totalRows);
            document.add(new Paragraph("\n"));
            document.add(tableRows);
            Paragraph content2 = new Paragraph();
            Paragraph p1 = new Paragraph("------- End of the BILL -------\n", fontAccountNo3);
            Paragraph p5 = new Paragraph("Total Amount : "+balance, fontAccountNo3);
            p1.setAlignment(Element.ALIGN_CENTER);
            p5.setAlignment(Element.ALIGN_RIGHT);
            content2.add(p5);
            content2.add(p1);
            document.add(content2);
            document.close();

            Toast.makeText(this, "Bill Downloaded Successfully", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Saved in " + fileName, Toast.LENGTH_SHORT).show();
            shrarefile(fileName);
           /* Intent i = new Intent(SellBillDetailsActivity.this, ProductSellActivity.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(i);
            finish();*/
        } catch (DocumentException e) {
            Toast.makeText(this, "2." + e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("err1", e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("err2", e.toString());
            Toast.makeText(this, "3." + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shrarefile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            Uri uri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share PDF"));
        } else {
            // Handle the case where the file doesn't exist
            // Show a message or log an error
        }
    }

    private void getShopkeeperID(String Value) {
        final ProgressDialog progressDialog = new ProgressDialog(SellBillDetailsActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetSellBill_Details(?)}");
                smt.setString("@date","");

                smt.execute();
                ResultSet rs = smt.getResultSet();
                if (rs.isBeforeFirst()){
                    while (rs.next()) {
                        arrayList_SCode.add(rs.getString("Phone"));
                        arrayList_SCodeName.add(rs.getString("Name")+"-"+rs.getString("Phone"));
                    }
                    activity_get_shopkeeper_list.setVisibility(VISIBLE);
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter(SellBillDetailsActivity.this,R.layout.spinner_hint, arrayList_SCodeName);
                    activity_get_shopkeeper_list.setAdapter(arrayAdapter);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(SellBillDetailsActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            }else{
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.d("bbc", "getShopkeeperDetails: "+e);
        }


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

         DatePickerDialog datePickerDialog = new DatePickerDialog(SellBillDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
        mPb_proggress.setVisibility(VISIBLE);
        cn = new SqlManager().getSQLConnection();
        mArrayListSellReport.clear();
        SetGetSellDetailsReport setGetSellDetailsReport = null;
         Log.e("Saleidd",""+username);
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call ADROID_GetSellBill_Datewise(?)}");
                smt.setString("@Saleid", username);
                smt.execute();
                ResultSet rs = smt.getResultSet();
                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        setGetSellDetailsReport = new SetGetSellDetailsReport();
                        setGetSellDetailsReport.setSaleDate(rs.getString("saledate"));
                        setGetSellDetailsReport.setSaleid(rs.getString("SaleID"));
                        setGetSellDetailsReport.setPayableAmt(rs.getString("salePrice"));
                        setGetSellDetailsReport.setCoustomerPh(rs.getString("BatchNo"));
                        setGetSellDetailsReport.setCoustomerName(rs.getString("Quantity"));
                        setGetSellDetailsReport.setItemID(rs.getString("ItemID"));
                        setGetSellDetailsReport.setItemName(rs.getString("ItemName"));
                        setGetSellDetailsReport.setBatchNo(rs.getString("MRP"));
                        setGetSellDetailsReport.setExpary(rs.getString("ExpiryDate"));

                        /* setGetBillReport.setQuantity(rs.getString("Quantity"));
                        setGetBillReport.setSalePrice(rs.getString("SalePrice"));

                        setGetBillReport.setBatchNo(rs.getString("BatchNo"));*/
                        totalAmount=totalAmount+Double.parseDouble(rs.getString("salePrice"));
                        mArrayListSellReport.add(setGetSellDetailsReport);



                    }
                    Btn_download.setVisibility(VISIBLE);
                    mTV_buyerNameTextView.setText(String.valueOf(totalAmount));
                    adapterSellReport = new AdapterBillDetailsReport(SellBillDetailsActivity.this, mArrayListSellReport);
                    mRv_loanDueReport.setAdapter(adapterSellReport);
                    mPb_proggress.setVisibility(View.GONE);
                   // adapterSellReport.notifyDataSetChanged();

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
                 startActivity(new Intent(SellBillDetailsActivity.this, SellBillActivity.class));
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
         startActivity(new Intent(SellBillDetailsActivity.this, SellBillActivity.class));
         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         finish();
     }
 }

