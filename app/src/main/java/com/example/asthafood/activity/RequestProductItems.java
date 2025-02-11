package com.example.asthafood.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.asthafood.R;
import com.example.asthafood.databinding.ActivityRequestProductItemsBinding;

public class RequestProductItems extends AppCompatActivity {

    ActivityRequestProductItemsBinding binding;

    String CategoryNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestProductItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        CategoryNo = getIntent().getStringExtra("categoryNo");



    }
}