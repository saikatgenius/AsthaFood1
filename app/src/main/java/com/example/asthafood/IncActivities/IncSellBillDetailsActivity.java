package com.example.asthafood.IncActivities;

import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import com.example.asthafood.R;
import com.example.asthafood.activity.SellBillActivity;
import com.example.asthafood.adapters.AdapterBillDetailsReport;
import com.example.asthafood.bean.GlobalStore;
import com.example.asthafood.mssql.SqlManager;
import com.example.asthafood.mssql.models.SellProductDetailsModel;
import com.example.asthafood.mssql.models.SetGetSellDetailsReport;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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
import java.util.Calendar;
import java.util.Date;

public class IncSellBillDetailsActivity extends AppCompatActivity implements View.OnClickListener {
     // toolbar
     private Toolbar mToolbar;
   // private ArrayList<SellProductDetailsModel> arrayList=new ArrayList<>();
     private TextView mToolbarTitle,mTV_buyerNameTextView;
    SellProductDetailsModel sellProductDetailsModel;
     private TextView mTv_fDate;
    String fileName="";
    private ProgressDialog progressDialog;
     private  AppCompatButton Btn_download;

     private  EditText Edt_txtSearchShopkeeper;
     private TextView mTv_tDate;
     private  String saleid="";
     private  String bill_id="";
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

         Intent intent = getIntent();
         saleid = intent.getStringExtra("Saleid");
         bill_id = saleid;
         BuyerName = intent.getStringExtra("CoustomerName");
         // Initialize progress dialog
         progressDialog = new ProgressDialog(this);
         progressDialog.setMessage("Loading...");
         progressDialog.setCancelable(false);
         // Get bill data first
         getCollReport(1, saleid);
         Log.e("Saleid1", "" + saleid);
         Log.e("bill_id1", "" + bill_id);

         // Try to generate PDF, but don't crash if it fails
         try {
             // Only attempt to download if we have data
             if (mArrayListSellReport != null && !mArrayListSellReport.isEmpty()) {


                 downloadSavingStatement(mArrayListSellReport, bill_id, BuyerName, "Customer Address");
             } else {
                 Log.w("PDF Generation", "No data to generate PDF");
                 // Wait for getCollReport to finish
             }
         } catch (Exception e) {
             Log.e("PDF Generation", "Error generating bill in onCreate", e);
             Toast.makeText(this, "Could not generate bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
         }

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
                 Toast.makeText(IncSellBillDetailsActivity.this,"Please Enter Value",Toast.LENGTH_LONG).show();
                 Edt_txtSearchShopkeeper.requestFocus();
             }
         } else if (v == Btn_download) {

             Toast.makeText(this, "Bill Downloaded Successfully", Toast.LENGTH_SHORT).show();
             Toast.makeText(this, "Saved in " + fileName, Toast.LENGTH_SHORT).show();
             shrarefile(fileName);


         }
     }

    private void downloadSavingStatement(ArrayList<SetGetSellDetailsReport> arrayList, String billNo, String partyName, String address) {

        progressDialog.show();
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        document.setPageSize(new Rectangle(595, 842)); // A4 size
        long time = System.currentTimeMillis();

        try {
            fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + ("BILL_" + billNo + "_" + time + ".pdf");
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
                // Load the app logo image with safer error handling
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo_bill);
                    if (bitmap == null) {
                        Log.e("PDF Generation", "Failed to decode app_logo resource");
                        // Continue without the logo
                    } else {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        try {
                            boolean compressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            if (!compressed) {
                                Log.e("PDF Generation", "Failed to compress bitmap");
                                // Continue without the logo
                            } else {
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
                            }
                        } catch (Exception e) {
                            Log.e("PDF Generation", "Error compressing bitmap: " + e.getMessage());
                            // Continue without the logo
                        } finally {
                            try {
                                stream.close();
                            } catch (Exception e) {
                                Log.e("PDF Generation", "Error closing stream: " + e.getMessage());
                            }
                        }
                    }
                } catch (OutOfMemoryError oom) {
                    Log.e("PDF Generation", "Out of memory when decoding logo", oom);
                    // Handle out of memory error
                    System.gc(); // Suggest garbage collection
                    // Continue without the logo
                }
            } catch (Exception e) {
                Log.e("PDF Generation", "Error adding logo to PDF: " + e.getMessage());
                // Continue without the logo - don't let logo issues prevent bill generation
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

            Paragraph partyNamePara = new Paragraph("Party Name: " + partyName, fontNormal);
            partyCell.addElement(partyNamePara);

            Paragraph addressPara = new Paragraph("Address: " + address, fontNormal);
            partyCell.addElement(addressPara);

            partyTable.addCell(partyCell);

            // Bill Info Cell
            PdfPCell billInfoCell = new PdfPCell();
            billInfoCell.setBorder(Rectangle.BOX);
            billInfoCell.setPadding(5);

            Paragraph billNoPara = new Paragraph("Bill No.: " + billNo, fontNormal);
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
                    SetGetSellDetailsReport item = arrayList.get(i);

                    slNoCell.addElement(new Paragraph(String.valueOf(slNo++), fontNormal));
                    particularsCell.addElement(new Paragraph(item.getItemName(), fontNormal));

                    // Assuming item.getCoustomerName() contains quantity (might need to be adjusted)
                    qtyCell.addElement(new Paragraph(item.getCoustomerName(), fontNormal));

                    // Parse price properly
                    double price = 0;
                    try {
                        price = Double.parseDouble(item.getPayableAmt());
                    } catch (NumberFormatException e) {
                        price = 0;
                    }

                    // Calculate quantity
                    double quantity = 1; // Default quantity
                    try {
                        quantity = Double.parseDouble(item.getCoustomerName());
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

            showPdfInImageView(fileName);
            setupPdfImageViewZoom();

            progressDialog.dismiss();


     /*   Toast.makeText(this, "Bill Downloaded Successfully", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Saved in " + fileName, Toast.LENGTH_SHORT).show();
        shrarefile(fileName);*/

        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, "Error generating bill: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }




    // Add this function to display the PDF in an ImageView
    // Improved showPdfInImageView method with better error handling
    private void showPdfInImageView(String pdfFilePath) {
        ImageView pdfImageView = findViewById(R.id.pdfImageView);
        ParcelFileDescriptor fileDescriptor = null;
        PdfRenderer renderer = null;
        PdfRenderer.Page page = null;

        try {
            File pdfFile = new File(pdfFilePath);
            if (!pdfFile.exists() || !pdfFile.canRead()) {
                Log.e("PDF View", "PDF file doesn't exist or can't be read: " + pdfFilePath);
                Toast.makeText(this, "Can't access PDF file", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a renderer for the PDF
            fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            renderer = new PdfRenderer(fileDescriptor);

            if (renderer.getPageCount() <= 0) {
                Log.e("PDF View", "PDF has no pages");
                Toast.makeText(this, "PDF has no pages to display", Toast.LENGTH_SHORT).show();
                return;
            }

            // Render the first page
            page = renderer.openPage(0);

            // Create a bitmap with the page dimensions (with error handling for large PDFs)
            Bitmap bitmap;
            try {
                bitmap = Bitmap.createBitmap(
                        page.getWidth() * 2, page.getHeight() * 2, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                Log.e("PDF View", "Out of memory creating bitmap for PDF", e);
                Toast.makeText(this, "PDF is too large to display", Toast.LENGTH_SHORT).show();
                return;
            }

            // Render the page onto the bitmap
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Set the bitmap to the ImageView
            pdfImageView.setImageBitmap(bitmap);

            // Make the ImageView visible
            pdfImageView.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("PDF View", "Error displaying PDF", e);
            Toast.makeText(this, "Error displaying PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Close resources in reverse order
            if (page != null) {
                try {
                    page.close();
                } catch (Exception e) {
                    Log.e("PDF View", "Error closing PDF page", e);
                }
            }
            if (renderer != null) {
                try {
                    renderer.close();
                } catch (Exception e) {
                    Log.e("PDF View", "Error closing PDF renderer", e);
                }
            }
            if (fileDescriptor != null) {
                try {
                    fileDescriptor.close();
                } catch (Exception e) {
                    Log.e("PDF View", "Error closing file descriptor", e);
                }
            }
        }
    }

    // Method to set up pinch-to-zoom functionality for the ImageView
    private void setupPdfImageViewZoom() {
        ImageView pdfImageView = findViewById(R.id.pdfImageView);

        // Set up the scale gesture detector
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    private float scaleFactor = 1.0f;
                    private final float MIN_SCALE = 0.5f;
                    private final float MAX_SCALE = 5.0f;

                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scaleFactor *= detector.getScaleFactor();
                        // Limit the scale factor
                        scaleFactor = Math.max(MIN_SCALE, Math.min(scaleFactor, MAX_SCALE));

                        // Apply the scale
                        pdfImageView.setScaleX(scaleFactor);
                        pdfImageView.setScaleY(scaleFactor);
                        return true;
                    }
                });

        // Set touch listener for the ImageView
        pdfImageView.setOnTouchListener(new View.OnTouchListener() {
            private float lastTouchX;
            private float lastTouchY;
            private float positionX;
            private float positionY;
            private static final int INVALID_POINTER_ID = -1;
            private int activePointerId = INVALID_POINTER_ID;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Let the scale gesture detector inspect the event
                scaleGestureDetector.onTouchEvent(event);

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        // Save the position for dragging
                        final int pointerIndex = event.getActionIndex();
                        final float x = event.getX(pointerIndex);
                        final float y = event.getY(pointerIndex);

                        lastTouchX = x;
                        lastTouchY = y;
                        activePointerId = event.getPointerId(0);
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        // Only move if we're not scaling
                        if (!scaleGestureDetector.isInProgress()) {
                            final int pointerIndex = event.findPointerIndex(activePointerId);
                            final float x = event.getX(pointerIndex);
                            final float y = event.getY(pointerIndex);

                            // Calculate the distance moved
                            final float dx = x - lastTouchX;
                            final float dy = y - lastTouchY;

                            // Update position
                            positionX += dx;
                            positionY += dy;

                            // Apply translation
                            pdfImageView.setTranslationX(positionX);
                            pdfImageView.setTranslationY(positionY);

                            // Remember this position for the next move event
                            lastTouchX = x;
                            lastTouchY = y;
                        }
                        break;
                    }

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        activePointerId = INVALID_POINTER_ID;
                        break;
                    }

                    case MotionEvent.ACTION_POINTER_UP: {
                        final int pointerIndex = event.getActionIndex();
                        final int pointerId = event.getPointerId(pointerIndex);

                        if (pointerId == activePointerId) {
                            // Choose a new active pointer
                            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                            lastTouchX = event.getX(newPointerIndex);
                            lastTouchY = event.getY(newPointerIndex);
                            activePointerId = event.getPointerId(newPointerIndex);
                        }
                        break;
                    }
                }
                return true;
            }
        });

        // Enable the view to handle touch events
        pdfImageView.setClickable(true);
        pdfImageView.setFocusable(true);
    }

// Modified downloadSavingStatement method - just add these two lines at the end of the try block
// before the final Toast messages
// showPdfInImageView(fileName);
// setupPdfImageViewZoom();


    // Helper method to convert number to words (for Rupees in words)
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


    // Improved shrarefile method with better error handling


    // Helper method for safely getting string values from ResultSet
    private String getStringOrEmpty(ResultSet rs, String columnName) {
        try {
            String value = rs.getString(columnName);
            return value != null ? value : "";
        } catch (Exception e) {
            Log.e("ResultSet", "Error getting column " + columnName + ": " + e.getMessage());
            return "";
        }
    }
    private void shrarefile(String fileName) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri uri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verify that there are apps to handle this intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Share PDF"));
            } else {
                Toast.makeText(this, "No app available to share PDF", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ShareFile", "Error sharing PDF: " + e.getMessage(), e);
            Toast.makeText(this, "Error sharing PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getShopkeeperID(String Value) {
        final ProgressDialog progressDialog = new ProgressDialog(IncSellBillDetailsActivity.this,
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
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter(IncSellBillDetailsActivity.this,R.layout.spinner_hint, arrayList_SCodeName);
                    activity_get_shopkeeper_list.setAdapter(arrayAdapter);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(IncSellBillDetailsActivity.this,"No Data Found",Toast.LENGTH_LONG).show();
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

         DatePickerDialog datePickerDialog = new DatePickerDialog(IncSellBillDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {
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



    public void getCollReport(int tDate, String username) {
        mTV_buyerNameTextView.setText("0");
        mPb_proggress.setVisibility(View.VISIBLE);

        // Execute the database query on a background thread
        new Thread(() -> {
            Connection connection = null;
            CallableStatement statement = null;
            ResultSet resultSet = null;

            try {
                connection = new SqlManager().getSQLConnection();
                if (connection == null) {
                    runOnUiThread(() -> {
                        mPb_proggress.setVisibility(View.GONE);
                        Toast.makeText(IncSellBillDetailsActivity.this, "Database connection failed", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                statement = connection.prepareCall("{call ADROID_GetSellBill_Datewise(?)}");
                statement.setString("@Saleid", username);
                statement.execute();
                resultSet = statement.getResultSet();

                if (resultSet != null && resultSet.isBeforeFirst()) {
                    mArrayListSellReport.clear();
                    double total = 0.0;

                    while (resultSet.next()) {
                        SetGetSellDetailsReport report = new SetGetSellDetailsReport();

                        // Safely get string values with null checks
                        report.setSaleDate(getStringOrEmpty(resultSet, "saledate"));
                        report.setSaleid(getStringOrEmpty(resultSet, "SaleID"));
                        report.setPayableAmt(getStringOrEmpty(resultSet, "salePrice"));
                        report.setCoustomerPh(getStringOrEmpty(resultSet, "BatchNo"));
                        report.setCoustomerName(getStringOrEmpty(resultSet, "Quantity"));
                        report.setItemID(getStringOrEmpty(resultSet, "ItemID"));
                        report.setItemName(getStringOrEmpty(resultSet, "ItemName"));
                        report.setBatchNo(getStringOrEmpty(resultSet, "MRP"));
                        report.setExpary(getStringOrEmpty(resultSet, "ExpiryDate"));
                        report.setItemDetails(getStringOrEmpty(resultSet, "ItemDetails"));
                        report.setBuyer(BuyerName);
                        Log.e("ItemName",""+getStringOrEmpty(resultSet, "ItemName"));

                        // Safely parse double values
                        try {
                            String priceStr = getStringOrEmpty(resultSet, "salePrice");
                            if (!priceStr.isEmpty()) {
                                total += Double.parseDouble(priceStr);
                            }
                        } catch (NumberFormatException e) {
                            Log.e("getCollReport", "Error parsing price: " + e.getMessage());
                        }

                        bill_id = getStringOrEmpty(resultSet, "SaleID");
                        mArrayListSellReport.add(report);
                    }

                    final double finalTotal = total;
                    runOnUiThread(() -> {
                        Btn_download.setVisibility(View.VISIBLE);
                        mTV_buyerNameTextView.setText(String.valueOf(finalTotal));
                        adapterSellReport = new AdapterBillDetailsReport(IncSellBillDetailsActivity.this, mArrayListSellReport);
                        mRv_loanDueReport.setAdapter(adapterSellReport);
                        mPb_proggress.setVisibility(View.GONE);

                        // Now that we have data, try to generate the PDF
                        if (!mArrayListSellReport.isEmpty()) {
                            try {
                                downloadSavingStatement(mArrayListSellReport, bill_id, BuyerName, "Customer Address");
                            } catch (Exception e) {
                                Log.e("PDF Generation", "Error generating bill after data load", e);
                                Toast.makeText(IncSellBillDetailsActivity.this,
                                        "Could not generate bill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        mPb_proggress.setVisibility(View.GONE);
                        Toast.makeText(IncSellBillDetailsActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception ex) {
                Log.e("getCollReport", "Database error: " + ex.getMessage(), ex);
                runOnUiThread(() -> {
                    mPb_proggress.setVisibility(View.GONE);
                    Toast.makeText(IncSellBillDetailsActivity.this,
                            "Database error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                // Close resources in reverse order
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (Exception e) {
                    Log.e("getCollReport", "Error closing database resources: " + e.getMessage());
                }
            }
        }).start();
    }




    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:
                 // todo: goto back activity from here
                 startActivity(new Intent(IncSellBillDetailsActivity.this, IncSellBillActivity.class));
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
         startActivity(new Intent(IncSellBillDetailsActivity.this, IncSellBillActivity.class));
         overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
         finish();
     }
 }

