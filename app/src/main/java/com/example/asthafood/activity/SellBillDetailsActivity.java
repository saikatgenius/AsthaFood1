package com.example.asthafood.activity;

import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
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
import com.example.asthafood.bean.AppData;
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



import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class SellBillDetailsActivity extends AppCompatActivity implements View.OnClickListener {
     // toolbar
     private Toolbar mToolbar;
   // private ArrayList<SellProductDetailsModel> arrayList=new ArrayList<>();
     private TextView mToolbarTitle,mTV_buyerNameTextView;
    SellProductDetailsModel sellProductDetailsModel;
     private TextView mTv_fDate;
    String fileName="";
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
       //  getLoanDueReport(1, 2, GlobalStore.GlobalValue.getUserName());


         Intent intent=getIntent();
        saleid= intent.getStringExtra("Saleid");
         bill_id=saleid;
         BuyerName=intent.getStringExtra("CoustomerName");


         getCollReport(1,saleid);
         Log.e("Saleid1",""+saleid);
         Log.e("bill_id1",""+bill_id);

         downloadSavingStatement(mArrayListSellReport, bill_id, saleid, "Customer Address");

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


           //  downloadSavingStatement(mArrayListSellReport);
            // downloadSavingStatement(mArrayListSellReport, bill_id, saleid, "Customer Address");

             Toast.makeText(this, "Bill Downloaded Successfully", Toast.LENGTH_SHORT).show();
             Toast.makeText(this, "Saved in " + fileName, Toast.LENGTH_SHORT).show();
             shrarefile(fileName);


         }
     }

    private void downloadSavingStatement(ArrayList<SetGetSellDetailsReport> arrayList, String billNo, String partyName, String address) {
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

     /*   Toast.makeText(this, "Bill Downloaded Successfully", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Saved in " + fileName, Toast.LENGTH_SHORT).show();
        shrarefile(fileName);*/

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating bill: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }




    // Add this function to display the PDF in an ImageView
    private void showPdfInImageView(String pdfFilePath) {
        try {
            // Get a reference to your ImageView (assuming you have one in your layout)
            ImageView pdfImageView = findViewById(R.id.pdfImageView);

            // Create a renderer for the PDF
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(
                    new File(pdfFilePath), ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);

            // Render the first page
            PdfRenderer.Page page = renderer.openPage(0);

            // Create a bitmap with the page dimensions
            Bitmap bitmap = Bitmap.createBitmap(
                    page.getWidth() * 2, page.getHeight() * 2, Bitmap.Config.ARGB_8888);

            // Render the page onto the bitmap (with higher resolution)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Set the bitmap to the ImageView
            pdfImageView.setImageBitmap(bitmap);

            // Close the page and renderer
            page.close();
            renderer.close();
            fileDescriptor.close();

            // Make the ImageView visible (in case it was previously invisible)
            pdfImageView.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error displaying PDF: " + e.toString(), Toast.LENGTH_SHORT).show();
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
/*    private void downloadSavingStatement(ArrayList<SetGetSellDetailsReport> arrayList) {

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


            PdfPCell c1 = new PdfPCell(new PdfPCell(new Paragraph("BILL  ASTHA FOOD\n\n", hf)));
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
           *//* Intent i = new Intent(SellBillDetailsActivity.this, ProductSellActivity.class);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            startActivity(i);
            finish();*//*
        } catch (DocumentException e) {
            Toast.makeText(this, "2." + e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("err1", e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("err2", e.toString());
            Toast.makeText(this, "3." + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }*/

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
                        setGetSellDetailsReport.setItemDetails(rs.getString("ItemDetails"));
                        Log.e("ItemDetails",""+rs.getString("ItemDetails"));
                        setGetSellDetailsReport.setBuyer(BuyerName);
                        bill_id=rs.getString("SaleID");


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

