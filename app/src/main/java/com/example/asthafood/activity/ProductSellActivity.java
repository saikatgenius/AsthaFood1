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
import com.example.asthafood.Util.GlobalReqProductList;
import com.example.asthafood.adapters.PreviewItemAdapter;
import com.example.asthafood.adapters.RequestCartAdapter;
import com.example.asthafood.adapters.SellProductDetailsAdapter;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.databinding.ActivityProductSellBinding;
import com.example.asthafood.databinding.PreviewBottomSheetBinding;
import com.example.asthafood.databinding.SellBottomSheetBinding;
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

public class ProductSellActivity extends AppCompatActivity {

    ActivityProductSellBinding binding;
    double TotalPrice =0.0;
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
                binding.submit.setVisibility(View.VISIBLE);
                binding.Preview.setVisibility(View.VISIBLE);
            }
        });

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double CheckPrice = 0.0;
                for (int i = 0 ; i<arrayList.size();i++){
                    CheckPrice += arrayList.get(i).getSellingQntyFinalPrice();
                }

                if (CheckPrice==Double.parseDouble(binding.grandTotal.getText().toString()) && binding.grandTotal.getText() != ""){
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

        binding.DownloadAndShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadSavingStatement(arrayList,BillNo);
            }
        });

        binding.Preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewItemAdapter previewItemAdapter;
                ArrayList<SellProductDetailsModel> arrayListNew=new ArrayList<>();

                BottomSheetDialog dialog =new  BottomSheetDialog(ProductSellActivity.this);
                PreviewBottomSheetBinding binding = PreviewBottomSheetBinding.inflate(LayoutInflater.from(ProductSellActivity.this));
                dialog.setContentView(binding.getRoot());

                LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(ProductSellActivity.this);
                binding.rvproductDetails.setLayoutManager(linearLayoutManager1);

                for (int i = 0 ; i<arrayList.size();i++){
                    if (arrayList.get(i).getSellingQnty()>0){
                        arrayListNew.add(arrayList.get(i));
                    }
                }

                previewItemAdapter = new PreviewItemAdapter(arrayListNew,ProductSellActivity.this);
                binding.rvproductDetails.setAdapter(previewItemAdapter);




                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();


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
                Log.d("ergergerg" + "", collectionList);
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
                 BillNo=smt.getString("@SaleID");
                if (ReturnERRORCode==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductSellActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Successful");
                    builder.setMessage("Product Sale Successfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            binding.submit.setVisibility(View.GONE);
                            binding.DownloadAndShare.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }
                    }).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductSellActivity.this);
                    builder.setCancelable(false);
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

    private void
    downloadSavingStatement(ArrayList<SellProductDetailsModel> arrayList,String billno) {
        Log.e("arrayList1",""+ arrayList.get(0).getProductID());
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

            leftRows.addCell(new Paragraph("BUYER NAME ", fontBold14));
            leftRows.addCell(":");
            leftRows.addCell("" + binding.shopkeeperName.getText().toString() + "\t\t\n\n");


            leftRows.addCell("" + GlobalStore.GlobalValue.getUserName() + "\t\t\n\n");
            leftRows.addCell(new Paragraph("SELLER NAME ", fontBold14));
            leftRows.addCell(":");
            leftRows.addCell("" + GlobalStore.GlobalValue.getUserOriginalName() + "\t\t\n\n");
            leftRows.addCell(new Paragraph("Bill No", fontBold14));
            leftRows.addCell(":");
            leftRows.addCell("" + billno + "\t\t\n\n");

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
                if (arrayList.get(i).getSellingQnty()>0){
                    Log.e("arrayList51",""+ arrayList.get(i).getProductID());
                    document.open();
                    Log.e("arrayList21",""+ arrayList.get(i).getProductID());

                  balance +=  arrayList.get(i).getSellingQntyFinalPrice();

                    totalRows.addCell("" + arrayList.get(i).getProductID());
                    totalRows.addCell("" + arrayList.get(i).getProductName());
                    totalRows.addCell("" + arrayList.get(i).getSellingQnty());
                    totalRows.addCell("" + arrayList.get(i).getSellingQntyFinalPrice());
                    totalRows.addCell("" + arrayList.get(i).getBatchNo());
                    totalRows.addCell("" + arrayList.get(i).getExpiryDate());
                    totalRows.addCell("" + arrayList.get(i).getMRP());


                }

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