package com.example.asthafood.IncActivities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.asthafood.MainActivity;
import com.example.asthafood.R;
import com.example.asthafood.adapters.PreviewItemAdapter;
import com.example.asthafood.adapters.SellProductDetailsAdapter;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.databinding.ActivityIncProductSellBinding;
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
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

public class IncProductSellActivity extends AppCompatActivity {
    ActivityIncProductSellBinding binding;
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
        binding = ActivityIncProductSellBinding.inflate(getLayoutInflater());



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
                    Toast.makeText(IncProductSellActivity.this,"Please Enter Value",Toast.LENGTH_LONG).show();
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
                        SubmitData(arrayList,(TotalPrice+TotalGST),TotalPrice);
                        if (AddShopKeeperFlag){
                            SubmitShopKeeper();

                        }

                    }else{
                        Toast.makeText(IncProductSellActivity.this,"Please Fill All ShopkeeperDetails",Toast.LENGTH_LONG).show();
                        binding.shopkeeperName.requestFocus();
                        binding.shopkeeperAddress.requestFocus();
                        binding.shopkeeperPhone.requestFocus();
                        binding.shopName.requestFocus();
                    }
                }else{
                    Toast.makeText(IncProductSellActivity.this,"Please Click GetAmount First",Toast.LENGTH_LONG).show();
                }

            }
        });

        binding.DownloadAndShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadSavingStatement1(arrayList,BillNo);
            }
        });

        binding.Preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewItemAdapter previewItemAdapter;
                ArrayList<SellProductDetailsModel> arrayListNew=new ArrayList<>();

                BottomSheetDialog dialog =new  BottomSheetDialog(IncProductSellActivity.this);
                PreviewBottomSheetBinding binding = PreviewBottomSheetBinding.inflate(LayoutInflater.from(IncProductSellActivity.this));
                dialog.setContentView(binding.getRoot());

                LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(IncProductSellActivity.this);
                binding.rvproductDetails.setLayoutManager(linearLayoutManager1);

                for (int i = 0 ; i<arrayList.size();i++){
                    if (arrayList.get(i).getSellingQnty()>0){
                        arrayListNew.add(arrayList.get(i));
                    }
                }

                previewItemAdapter = new PreviewItemAdapter(arrayListNew,IncProductSellActivity.this);
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
                    Toast.makeText(IncProductSellActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            }else{
                Log.d("bbc", "getShopkeeperDetails: Error ");
            }
        } catch (Exception e) {

            Log.d("bbc", "getShopkeeperDetails: "+e);
        }

    }

    private void getShopkeeperID(String Value) {
        final ProgressDialog progressDialog = new ProgressDialog(IncProductSellActivity.this,
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
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter(IncProductSellActivity.this,R.layout.spinner_hint, arrayList_SCodeName);
                    binding.spActivityGetShopkeeperList.setAdapter(arrayAdapter);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(IncProductSellActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            }else{
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.d("bbc", "getShopkeeperDetails: "+e);
        }


    }

    private void SubmitData(ArrayList<SellProductDetailsModel> arrayList, double totalPriceWGst,double TotalPrice) {
        String collectionList = "";
        for (int i = 0 ; i<arrayList.size();i++){
            if (arrayList.get(i).getSellingQnty()>0){
                collectionList +=
                        arrayList.get(i).getProductID()+","+arrayList.get(i).getProductName() + "," + arrayList.get(i).getSellingQnty()
                                + "," + arrayList.get(i).getSellingQntyFinalPrice()+"," +arrayList.get(i).getBatchNo()+","
                                +arrayList.get(i).getExpiryDate()+"," +arrayList.get(i).getMRP()+","+arrayList.get(i).getVoucherNo()+","
                            +arrayList.get(i).getGST()+","+arrayList.get(i).getGstPrice()+","+arrayList.get(i).getSellPrice()+","
                                +arrayList.get(i).getPrice()
                                +";";
                Log.d("ergergerg" + "", collectionList);
            }

        }
        final ProgressDialog progressDialog = new ProgressDialog(IncProductSellActivity.this,
                ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Connection cn = new SqlManager().getSQLConnection();
        try {
            if (cn != null) {
                CallableStatement smt = cn.prepareCall("{call USP_ADROID_INSERT_SELLING_PRO_INC(?,?,?,?,?,?,?,?,?,?,?)}");
                smt.setString("@UserName",GlobalStore.GlobalValue.getUserName());
                smt.setString("@CollectionList",collectionList);
                smt.setString("@CustomerName",binding.shopkeeperName.getText().toString());
                smt.setString("@CustomerPhn",binding.shopkeeperPhone.getText().toString());
                smt.setString("@CustomerAddrs",binding.shopkeeperAddress.getText().toString());
                smt.setString("@TotalAmountWithGst",String.valueOf(totalPriceWGst));
                smt.setString("@TotalAmount",String.valueOf(TotalPrice));
                smt.setString("@GSTAmount",String.valueOf(TotalGST));

                smt.registerOutParameter("@ReturnVoucherNo",java.sql.Types.VARCHAR);
                smt.registerOutParameter("@SaleID",java.sql.Types.VARCHAR);
                smt.registerOutParameter("@ErrorCode",java.sql.Types.INTEGER);
                smt.executeUpdate();
                int ReturnERRORCode  = smt.getInt("@ErrorCode");
                BillNo=smt.getString("@SaleID");
                if (ReturnERRORCode==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(IncProductSellActivity.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(IncProductSellActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Unable to enter Data");
                    builder.setMessage("Product Sale UnSuccessfully. ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //builder.setCancelable(true);
                            Intent i = new Intent(IncProductSellActivity.this, IncProductSellActivity.class);
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

    
    private void downloadSavingStatement1(ArrayList<SellProductDetailsModel> arrayList, String billno) {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        document.setPageSize(new Rectangle(595, 842)); // A4 size
        long time = System.currentTimeMillis();

        try {
            String fileName = "";
            fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + ("BILL_" + billno + "_" + time + ".pdf");
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));

            // Fonts
            Font fontTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font fontHeader = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
            Font fontFooter = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Font fontSmall = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

            document.open();

            // Border for the entire page
            PdfContentByte cb = writer.getDirectContent();
            Rectangle rect = new Rectangle(30, 30, 565, 812);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(1);
            cb.rectangle(rect);
            cb.stroke();

            // Header Section with Logo and Title
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            try {
                headerTable.setWidths(new float[]{1.5f, 5f, 2.5f});
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // Logo Cell
            PdfPCell logoCell = new PdfPCell();
            logoCell.setBorder(Rectangle.NO_BORDER);

            // Add "GOOD FOOD" text
            Paragraph logoText = new Paragraph("", fontHeader);
            logoText.setAlignment(Element.ALIGN_CENTER);
            logoCell.addElement(logoText);

            // Add logo image below the text
            try {
                // Load the app logo image
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapData = stream.toByteArray();
                Image logo = Image.getInstance(bitmapData);

                // Set the image size to fit within the cell (adjust width as needed)
                float maxWidth = 60f; // Maximum width of the image
                float ratio = logo.getWidth() / logo.getHeight();
                logo.scaleToFit(maxWidth, maxWidth / ratio);

                // Center the image
                logo.setAlignment(Element.ALIGN_CENTER);

                // Add some spacing after the text
                logoText.setSpacingAfter(5f);

                // Add the image to the cell
                logoCell.addElement(logo);
            } catch (Exception e) {
                e.printStackTrace();
                // If image loading fails, just continue without the image
            }

            headerTable.addCell(logoCell);

            // Title Cell
            PdfPCell titleCell = new PdfPCell();
            titleCell.setBorder(Rectangle.NO_BORDER);
            Paragraph title = new Paragraph("BILL/CASH MEMO", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            titleCell.addElement(title);
            Paragraph companyName = new Paragraph("Astha Foods", fontTitle);
            companyName.setAlignment(Element.ALIGN_CENTER);
            titleCell.addElement(companyName);
            headerTable.addCell(titleCell);

            // FSSAI Cell
            PdfPCell fssaiCell = new PdfPCell();
            fssaiCell.setBorder(Rectangle.NO_BORDER);
            Paragraph fssai = new Paragraph("fssai", fontSmall);
            fssai.setAlignment(Element.ALIGN_RIGHT);
            fssaiCell.addElement(fssai);
            Paragraph fssaiNo = new Paragraph("FSSAI No.: 22824130000827", fontNormal);
            fssaiNo.setAlignment(Element.ALIGN_RIGHT);
            fssaiCell.addElement(fssaiNo);
            headerTable.addCell(fssaiCell);

            document.add(headerTable);

            // Address Section
            PdfPTable addressTable = new PdfPTable(1);
            addressTable.setWidthPercentage(100);

            PdfPCell addressCell = new PdfPCell();
            addressCell.setBorder(Rectangle.BOX);
            addressCell.setPadding(5);

            Paragraph address1 = new Paragraph("H.O.- Noapara, Barasat, Kol.- 125, W.B.", fontNormal);
            address1.setAlignment(Element.ALIGN_CENTER);
            addressCell.addElement(address1);

            Paragraph address2 = new Paragraph("Mob.: 9732732396, 9330847747", fontNormal);
            address2.setAlignment(Element.ALIGN_CENTER);
            addressCell.addElement(address2);

            Paragraph address3 = new Paragraph("T. Licence: 0917P1142824189786", fontNormal);
            address3.setAlignment(Element.ALIGN_CENTER);
            addressCell.addElement(address3);

            addressTable.addCell(addressCell);
            document.add(addressTable);

            // Party Details and Bill Info
            PdfPTable partyTable = new PdfPTable(2);
            partyTable.setWidthPercentage(100);
            try {
                partyTable.setWidths(new float[]{3f, 1f});
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // Party Details Cell
            PdfPCell partyCell = new PdfPCell();
            partyCell.setBorder(Rectangle.BOX);
            partyCell.setPadding(5);

            Paragraph partyNamePara = new Paragraph("Party Name: " + binding.shopkeeperName.getText().toString(), fontNormal);
            partyCell.addElement(partyNamePara);

            Paragraph addressPara = new Paragraph("Address: " + binding.shopkeeperAddress.getText().toString(), fontNormal);
            partyCell.addElement(addressPara);

            partyTable.addCell(partyCell);

            // Bill Info Cell
            PdfPCell billInfoCell = new PdfPCell();
            billInfoCell.setBorder(Rectangle.BOX);
            billInfoCell.setPadding(5);

            Paragraph billNoPara = new Paragraph("Bill No.: " + billno, fontNormal);
            billInfoCell.addElement(billNoPara);

            Paragraph datePara = new Paragraph("Date: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), fontNormal);
            billInfoCell.addElement(datePara);

            partyTable.addCell(billInfoCell);
            document.add(partyTable);

            // Item Details Table
            PdfPTable itemTable = new PdfPTable(5);
            itemTable.setWidthPercentage(100);
            try {
                itemTable.setWidths(new float[]{0.5f, 3f, 0.7f, 0.7f, 1.1f});
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // Table Headers
            PdfPCell slNoHeader = new PdfPCell(new Phrase("Sl.\nNo.", fontHeader));
            slNoHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            slNoHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            slNoHeader.setBorder(Rectangle.BOX);
            itemTable.addCell(slNoHeader);

            PdfPCell particularsHeader = new PdfPCell(new Phrase("PARTICULARS", fontHeader));
            particularsHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            particularsHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            particularsHeader.setBorder(Rectangle.BOX);
            itemTable.addCell(particularsHeader);

            PdfPCell qtyHeader = new PdfPCell(new Phrase("Qnty.", fontHeader));
            qtyHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            qtyHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            qtyHeader.setBorder(Rectangle.BOX);
            itemTable.addCell(qtyHeader);

            PdfPCell rateHeader = new PdfPCell(new Phrase("Rate", fontHeader));
            rateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            rateHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            rateHeader.setBorder(Rectangle.BOX);
            itemTable.addCell(rateHeader);

            PdfPCell amountHeader = new PdfPCell();
            amountHeader.setBorder(Rectangle.BOX);

            PdfPTable amountSubTable = new PdfPTable(2);
            amountSubTable.setWidthPercentage(100);
            try {
                amountSubTable.setWidths(new float[]{3f, 1f});
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            PdfPCell rsHeader = new PdfPCell(new Phrase("Rs.", fontHeader));
            rsHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            rsHeader.setBorder(Rectangle.NO_BORDER);
            amountSubTable.addCell(rsHeader);

            PdfPCell pHeader = new PdfPCell(new Phrase("P.", fontHeader));
            pHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            pHeader.setBorder(Rectangle.NO_BORDER);
            amountSubTable.addCell(pHeader);

            amountHeader.addElement(new Paragraph("AMOUNT", fontHeader));
            amountHeader.addElement(amountSubTable);
            amountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

            itemTable.addCell(amountHeader);

            // Add items from arrayList
            double totalAmount = 0.0;
            int slNo = 1;

            // Add at least 10-15 empty rows if arrayList is small
            int minRows = Math.max(15, arrayList.size());

            for (int i = 0; i < minRows; i++) {
                PdfPCell slNoCell = new PdfPCell();
                PdfPCell particularsCell = new PdfPCell();
                PdfPCell qtyCell = new PdfPCell();
                PdfPCell rateCell = new PdfPCell();
                PdfPCell amountCell = new PdfPCell();

                slNoCell.setBorder(Rectangle.BOX);
                particularsCell.setBorder(Rectangle.BOX);
                qtyCell.setBorder(Rectangle.BOX);
                rateCell.setBorder(Rectangle.BOX);
                amountCell.setBorder(Rectangle.BOX);

                // If we have data for this row, add it
                if (i < arrayList.size()) {
                    SellProductDetailsModel item = arrayList.get(i);

                    slNoCell.addElement(new Paragraph(String.valueOf(slNo++), fontNormal));
                    particularsCell.addElement(new Paragraph(item.getProductName(), fontNormal));

                    // Assuming item.getCoustomerName() contains quantity (might need to be adjusted)
                    qtyCell.addElement(new Paragraph(String.valueOf(item.getSellingQnty()), fontNormal));

                    // Parse price properly
                    double price = 0;
                    try {
                        price = Double.parseDouble(String.valueOf(item.getSellPrice()));
                    } catch (NumberFormatException e) {
                        price = 0;
                    }

                    // Calculate quantity
                    double quantity = 1; // Default quantity
                    try {
                        quantity = Double.parseDouble(String.valueOf(item.getSellingQntyFinalPrice()));
                    } catch (NumberFormatException e) {
                        quantity = 1;
                    }

                    double rate = quantity > 0 ? price / quantity : price;

                    rateCell.addElement(new Paragraph(String.format("%.2f", rate), fontNormal));
                    amountCell.addElement(new Paragraph(String.format("%.2f", price), fontNormal));

                    totalAmount += price;
                }

                slNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                rateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                itemTable.addCell(slNoCell);
                itemTable.addCell(particularsCell);
                itemTable.addCell(qtyCell);
                itemTable.addCell(rateCell);
                itemTable.addCell(amountCell);
            }

            // Total Row
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL", fontHeader));
            totalLabelCell.setColspan(4);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            totalLabelCell.setBorder(Rectangle.BOX);
            itemTable.addCell(totalLabelCell);

            PdfPCell totalValueCell = new PdfPCell(new Phrase(String.format("%.2f", totalAmount), fontHeader));
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalValueCell.setBorder(Rectangle.BOX);
            itemTable.addCell(totalValueCell);

            document.add(itemTable);

            // Footer
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            try {
                footerTable.setWidths(new float[]{3f, 1f});
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            // Rupees in words
            PdfPCell rupeesCell = new PdfPCell();
            rupeesCell.setBorder(Rectangle.NO_BORDER);
            rupeesCell.setPadding(5);

            Paragraph rupeesPara = new Paragraph("Rupees (in word): " + numberToWords((int)totalAmount), fontNormal);
            rupeesCell.addElement(rupeesPara);

            // Add dots for signature line
            Paragraph dots = new Paragraph("\n\n\n.......................This is computer generated and no signature is required.......................", fontNormal);
            rupeesCell.addElement(dots);

            footerTable.addCell(rupeesCell);

            // For Astha Foods
            PdfPCell signatureCell = new PdfPCell();
            signatureCell.setBorder(Rectangle.NO_BORDER);
            signatureCell.setPadding(5);

            Paragraph signaturePara = new Paragraph("For Astha Foods", fontNormal);
            signaturePara.setAlignment(Element.ALIGN_RIGHT);
            signatureCell.addElement(signaturePara);

            footerTable.addCell(signatureCell);

            document.add(footerTable);

            document.close();
            shrarefile(fileName);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating bill: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    
    private String numberToWords(int number) {
        String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
        String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

        if (number == 0) {
            return "Zero";
        }

        if (number < 20) {
            return units[number];
        }

        if (number < 100) {
            return tens[number / 10] + (number % 10 != 0 ? " " + units[number % 10] : "");
        }

        if (number < 1000) {
            return units[number / 100] + " Hundred" + (number % 100 != 0 ? " and " + numberToWords(number % 100) : "");
        }

        if (number < 100000) {
            return numberToWords(number / 1000) + " Thousand" + (number % 1000 != 0 ? " " + numberToWords(number % 1000) : "");
        }

        if (number < 10000000) {
            return numberToWords(number / 100000) + " Lakh" + (number % 100000 != 0 ? " " + numberToWords(number % 100000) : "");
        }

        return numberToWords(number / 10000000) + " Crore" + (number % 10000000 != 0 ? " " + numberToWords(number % 10000000) : "");
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
                CallableStatement smt = cn.prepareCall("{call ADROID_GetSellItems_Inc(?)}");
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
                        sellProductDetailsModel.setPrice(rs.getString("IncEmpSellPrice"));
                        sellProductDetailsModel.setRemainingQuntity(rs.getInt("RemianingQnty"));
                        sellProductDetailsModel.setProductName(rs.getString("ProductName"));
                        sellProductDetailsModel.setMRP(rs.getString("MRP"));
                        sellProductDetailsModel.setExpiryDate(rs.getString("ExpiryDate"));
                        sellProductDetailsModel.setGST(rs.getString("TotalGST"));
                        sellProductDetailsModel.setVoucherNo(rs.getString("VoucherNo"));

                        sellProductDetailsModel.setIsNew(rs.getString("NewProduct"));
                        arrayList.add(sellProductDetailsModel);
                    }

                }
                sellProductDetailsAdapter = new SellProductDetailsAdapter( arrayList,IncProductSellActivity.this,"ProductSell");
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
                startActivity(new Intent(IncProductSellActivity.this, IncDashboardActivity.class));
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
        startActivity(new Intent(IncProductSellActivity.this, IncDashboardActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}