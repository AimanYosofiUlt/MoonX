package com.ewu.moonx.UI.RequestPkj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.ewu.moonx.R;

import java.util.Objects;

public class RequestMgrActivity extends AppCompatActivity {

    RelativeLayout addRequestBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_request_mgr);
        init();
        initEvent();
    }

    private void init() {
        addRequestBtn = findViewById(R.id.addRequestBtn);
    }

    private void initEvent() {
        addRequestBtn.setOnClickListener(v -> {

        });
    }
}