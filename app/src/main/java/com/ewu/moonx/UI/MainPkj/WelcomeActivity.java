package com.ewu.moonx.UI.MainPkj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ewu.moonx.R;
import com.ewu.moonx.UI.LoginPkj.LoginActivity;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {

    private final int PERMISSON_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.loginBtn).setOnClickListener(view -> {
            int width = findViewById(R.id.parentL).getWidth() - findViewById(R.id.bottomImg).getWidth();
            findViewById(R.id.topImg).animate().translationX(-width);
            findViewById(R.id.bottomImg).animate().translationX(width).withEndAction(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    WelcomeActivity.this.startActivity(mainIntent);
                    WelcomeActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    WelcomeActivity.this.finish();
                }
            });
        });

        findViewById(R.id.sendMsgsBtn).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startChatting();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSON_CODE);
                }
            }
        });
    }

    private void startChatting() {
        StartPublicChattingHandler handler = new StartPublicChattingHandler(this);
        handler.startChatting();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSON_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startChatting();
            }
        }
    }


}