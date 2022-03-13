package com.ewu.moonx.UI.LoginPkj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dd.processbutton.iml.ActionProcessButton;
import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Models.UserConfig;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.Pojo.DB.Tables.SettingTable;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.MainPkj.StartPublicChattingHandler;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class Login_DoneActivity extends AppCompatActivity {
    private final int PERMISSON_CODE = 1;
    ActionProcessButton verifyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__done);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        verifyBtn = findViewById(R.id.verifyBtn);

        if (isUserAllowed())
            showSetUpImageAcvitiy();
        else
            initEvent();
    }

    private void showSetUpImageAcvitiy() {
        Intent intent = new Intent(Login_DoneActivity.this, Login_setUserImgActivity.class);
        startActivity(intent);
        this.finish();
    }

    private boolean isUserAllowed() {
        SettingTable users = new SettingTable(this);
        Cursor cursor = DB.select(users.allowUserCol).from(users).start();
        cursor.moveToNext();
        return cursor.getString(0).equals(users.hisAllowed_WithoutImg);
    }

    private void initEvent() {
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyBtn.setProgress(Static.LOADING);
                verifyNow();
            }

            private void verifyNow() {
                if (Status.isNetConnected(Login_DoneActivity.this))
                    Status.showErrorMessage(Login_DoneActivity.this, verifyBtn, getString(R.string.no_internet_connection));
                else {
                    Firebase.FireCloudRef(Str.UsersConf).document(Static.getUid()).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    deleteConfDoc(documentSnapshot);
                                }

                                private void deleteConfDoc(DocumentSnapshot documentSnapshot) {
                                    Firebase.FireCloudRef(Str.UsersConf).document(Static.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            showActivity(Objects.requireNonNull(documentSnapshot.toObject(UserConfig.class)));
                                        }
                                    }).addOnFailureListener(e -> Status.showErrorMessage(Login_DoneActivity.this, verifyBtn, getString(R.string.weak_internet_connection)));
                                }
                            })
                            .addOnFailureListener(e -> Status.showErrorMessage(Login_DoneActivity.this, verifyBtn, getString(R.string.weak_internet_connection)));


//                showActivity(
//                    new    UserConfig(Static.getUid(), "AdminType")
//                );

                }
            }

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

    @Override
    protected void onStart() {
        super.onStart();

        Firebase.FireCloudRef(Str.UsersConf).document(Static.getUid())
                .addSnapshotListener(this, (value, error) -> {
                    assert value != null;
                    if (value.exists()) {
                        UserConfig userConfig = Objects.requireNonNull(value.toObject(UserConfig.class));
                        if (userConfig.getUid().equals(Static.getUid())) {
                            Firebase.FireCloudRef(Str.UsersConf).document(Static.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showActivity(userConfig);
                                }
                            }).addOnFailureListener(e -> Status.showErrorMessage(Login_DoneActivity.this, verifyBtn, getString(R.string.weak_internet_connection)));
                            showActivity(userConfig);
                        }
                    }
                });
    }

private void showActivity(UserConfig userConfig) {
        if (userConfig.getUid().equals(Static.getUid())) {
            Intent intent = new Intent(Login_DoneActivity.this, Login_setUserImgActivity.class);

            SettingTable userTable = new SettingTable(Login_DoneActivity.this);

            DB.set(userTable.typeCol, userConfig.getType())
                    .set(userTable.allowUserCol, userTable.hisAllowed_WithoutImg).update(userTable).exec();
            DB.delete(new PublicMessagesTable(Login_DoneActivity.this)).exec();

            Login_DoneActivity.this.startActivity(intent);
            Login_DoneActivity.this.finish();
        }
    }

    private void showActivityToDelete(UserConfig userConfig) {
        if (userConfig.getUid().equals(Static.getUid())) {
            Intent intent = new Intent(Login_DoneActivity.this, Login_setUserImgActivity.class);

            SettingTable userTable = new SettingTable(Login_DoneActivity.this);

            DB.set(userTable.typeCol, userConfig.getType())
                    .set(userTable.allowUserCol, userTable.hisAllowed_WithoutImg).update(userTable).exec();
            DB.delete(new PublicMessagesTable(Login_DoneActivity.this)).exec();

            Login_DoneActivity.this.startActivity(intent);
            Login_DoneActivity.this.finish();
        }
    }

    private void animateProgress() {
        findViewById(R.id.send_message).animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.send_progress).animate().alpha(1);
            }
        });
    }

    private void animateFieldProgress() {
        findViewById(R.id.send_progress).animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.send_message).animate().alpha(1);
            }
        });
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

    private void startChatting() {
        StartPublicChattingHandler handler = new StartPublicChattingHandler(this);

        SettingTable userTable = new SettingTable(this);

        Cursor cursor = DB.select(userTable.firstNameCol).select(userTable.thirdNameCol).from(userTable).start();
        cursor.moveToNext();
        String name = cursor.getString(0) + " " + cursor.getString(1);

        handler.startChatting(Static.getUid(), name);
    }
}