package com.ewu.moonx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ScrollView mainSC;
    RelativeLayout requestBtn;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_mgr);
        Objects.requireNonNull(getSupportActionBar()).hide();

        arrayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.add("Last Month");
        arrayAdapter.add("Last Week");
        arrayAdapter.add("Last Day");

        ((Spinner) findViewById(R.id.timeSpinner)).setAdapter(arrayAdapter);
    }


    private void init() {
        mainSC = findViewById(R.id.mainSC);
        requestBtn = findViewById(R.id.requestBtn);
    }

    private void initEvent() {
    }
}