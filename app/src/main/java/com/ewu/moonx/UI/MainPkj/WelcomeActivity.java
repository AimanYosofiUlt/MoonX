package com.ewu.moonx.UI.MainPkj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ewu.moonx.R;
import com.ewu.moonx.UI.LoginPkj.LoginActivity;

import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inti();
        initEvent();

    }

    private void initEvent() {
        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

            }
        });
    }

    private void Trian() {

    }

    private void inti() {

    }
}