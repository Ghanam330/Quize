package com.example.quaiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    Button btnStart, btnBookMarks;
    private AdView mAdView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.start_btn);
        btnBookMarks = findViewById(R.id.bookmarks_btn);


        MobileAds.initialize(this);
        loadADS();


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(categoryIntent);
            }
        });
        btnBookMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryIntent = new Intent(MainActivity.this, BookMarkActivity.class);
                startActivity(categoryIntent);
            }
        });
    }

    private void loadADS() {
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}