package com.ewu.moonx.UI.MainPkj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ewu.moonx.App.ScalingUtilities;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.SettingTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.PublicService.PublicChatService;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.UserService.UserChatService;
import com.ewu.moonx.UI.FollowUpPkj.FollowUpActivity;
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

//        DB.delete(new MessageTable(this)).exec();

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
            chatIntent.putExtra(Static.UserType, SettingTable.hisEmpAdmin);
            startService(chatIntent);
        }

        if (!UserChatService.isRunning) {
            Intent chatIntent = new Intent(this, UserChatService.class);
            startService(chatIntent);
        }
    }

    private void initEvent() {
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 12);
            }
        });

        empFollowBtn.setOnClickListener(v -> startActivity(FollowUpActivity.class));

        userMgrBtn.setOnClickListener(v -> startActivity(UserActivity.class));

    }


    private void startActivity(final Class activityClass) {
        Intent intent = new Intent(MainActivity.this, activityClass);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            Toast.makeText(this, "comprassion Done " +
                            ScalingUtilities.decodeFile(MainActivity.this, getRealPathFromURI(
                                    MainActivity.this, data.getData()), 50, 50)
                    , Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "result error", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj,
                null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}