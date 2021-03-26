package com.ewu.moonx.UI.MainPkj;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.LoginPkj.Login_DoneActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        DB.delete(new PublicMessagesTable(this)).exec();
        init();
    }

    private void init() {
        moveLogoImage();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getNextIntent();
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                SplashActivity.this.finish();
            }
        }, 2000);
    }

    private void moveLogoImage() {
        ImageView image = findViewById(R.id.image);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        image.startAnimation(animation);
    }

    private Intent getNextIntent() {
        Intent intent;
        if (isUserExist()) {
            if (isUserAllowed())
                    intent = new Intent(SplashActivity.this, MainActivity.class);
            else
                intent = new Intent(SplashActivity.this, Login_DoneActivity.class);

        } else {
            intent = new Intent(SplashActivity.this, WelcomeActivity.class);
        }
        return intent;
    }

    private boolean isUserAllowed() {
        UsersTable users = new UsersTable(this);
        Cursor cursor = DB.select(users.allowUserCol).from(users).start();
        cursor.moveToNext();
        return cursor.getString(0).equals(users.hisAllowed_WithImg);
    }

    private boolean isAdminType() {
        return false;
    }

    private boolean isUserExist() {
        boolean isUserExist = false;
        Cursor cursor = DB.selectAll().from(new UsersTable(this)).start();
        if (cursor != null && cursor.getCount() > 0) {
            isUserExist = true;
        }
        return isUserExist;
    }
}

