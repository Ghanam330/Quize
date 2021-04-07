package com.example.quaiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookMarkActivity extends AppCompatActivity {
    private static final String FILE_NAME = "QUIZZER";
    private static final String KEY_NAME = "QUESTIONS";
    RecyclerView recyclerView;
    private AdView adView;
    private List<QuestionModel> bookmarketsList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("BookMark");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.recycler);


        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();
        getBookmarks();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        BookMarkAdapter adapter = new BookMarkAdapter(bookmarketsList);
        recyclerView.setAdapter(adapter);


        //   MobileAds.initialize(this);
        loadADS();
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    public void getBookmarks() {
        String json = preferences.getString(KEY_NAME, "");
        Type type = new TypeToken<List<QuestionModel>>() {
        }.getType();
        bookmarketsList = gson.fromJson(json, type);

        if (bookmarketsList == null) {
            bookmarketsList = new ArrayList<>();
        }
    }

    private void storeBookmarks() {
        String json = gson.toJson(bookmarketsList);
        editor.putString(KEY_NAME, json);
        editor.commit();

    }

    private void loadADS() {
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}