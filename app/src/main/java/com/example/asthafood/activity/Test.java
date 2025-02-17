package com.example.asthafood.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asthafood.R;

public class Test extends AppCompatActivity {


    private EditText SearchLoan;
    private Button btnShopkepper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);

        SearchLoan=findViewById(R.id.txtSearchLoan);
        btnShopkepper==findViewById(R.id.btnSearchShopkepper);


        btnShopkepper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchLoan.getText().toString().trim();
            }
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}