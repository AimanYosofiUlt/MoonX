package com.ewu.moonx.UI.FollowUpPkj.ChatPkj;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.ewu.moonx.R;

import java.util.Objects;

public class SendImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_images);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
}