package com.ewu.moonx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class LinksActivity extends AppCompatActivity {
    LinksLoupView  loupView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);
        loupView = findViewById(R.id.loup);
        ArrayList<View>  views = new ArrayList<>();
        loupView.addView(findViewById(R.id.s1).findViewById(R.id.searchBtn),views);
    }
}