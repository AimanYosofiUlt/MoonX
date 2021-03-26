package com.ewu.moonx.UI.MainPkj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.PublicChatService;
import com.ewu.moonx.UI.FollowUpPkj.FollowUpActivity;
import com.ewu.moonx.UI.RequestPkj.RequestMgrActivity;
import com.ewu.moonx.UI.UserPkj.UserActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    RelativeLayout requestBtn, empFollowBtn, userMgrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();
        initEvent();
    }

    private void init() {
        requestBtn = findViewById(R.id.requestBtn);
        empFollowBtn = findViewById(R.id.empfollowBtn);
        userMgrBtn = findViewById(R.id.userMgrBtn);
        startServices();
    }

    private void startServices() {
        Log.d(TAG, "startServices: startServices");
        if (!PublicChatService.isRunning) {
            Intent chatIntent = new Intent(this, PublicChatService.class);
            chatIntent.putExtra(Static.UserType, UsersTable.hisEmpAdmin);
            startService(chatIntent);
        }
    }

    private void initEvent() {
        requestBtn.setOnClickListener(v -> startActivity(RequestMgrActivity.class));

        empFollowBtn.setOnClickListener(v -> startActivity(FollowUpActivity.class));

        userMgrBtn.setOnClickListener(v -> startActivity(UserActivity.class));

    }



    private void startActivity(final Class activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}