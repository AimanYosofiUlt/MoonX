package com.ewu.moonx.UI.LoginPkj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.processbutton.iml.ActionProcessButton;
import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.PublicVariable;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Template.Str;
import com.ewu.moonx.Pojo.DB.Template.UserConfig;
import com.ewu.moonx.Pojo.DB.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.MainPkj.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class Login_DoneActivity extends AppCompatActivity {
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
        initEvent();
    }

    private void initEvent() {
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyBtn.setProgress(PublicVariable.LOADING);
                verifyNow();
            }

            private void verifyNow() {
                if (Status.isNetConnected(Login_DoneActivity.this))
                    Status.showErrorMessage(Login_DoneActivity.this, verifyBtn, getString(R.string.no_internet_connection));
                else
                    Firebase.FireCloudRef(Str.UsersConf).document(PublicVariable.getUid()).get()
                            .addOnSuccessListener(onSucesss())
                            .addOnFailureListener(onFailure());
            }

            private OnFailureListener onFailure() {
                return e -> Status.showErrorMessage(Login_DoneActivity.this, verifyBtn, getString(R.string.weak_internet_connection));
            }

            private OnSuccessListener<? super DocumentSnapshot> onSucesss() {
                return new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        showActivity(Objects.requireNonNull(documentSnapshot.toObject(UserConfig.class)));
                    }
                };
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Firebase.FireCloudRef(Str.UsersConf).document(PublicVariable.getUid())
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        showActivity(Objects.requireNonNull(value.toObject(UserConfig.class)));
                    }
                });
    }

    private void showActivity(UserConfig userConfig) {
        if (userConfig.getUid().equals(PublicVariable.getUid())) {
            Intent intent= new Intent(Login_DoneActivity.this, MainActivity.class);

            UsersTable usersTable = new UsersTable(Login_DoneActivity.this);

            DB.set(usersTable.typeCol, userConfig.getType())
                    .set(usersTable.allowUserCol, usersTable.hisAllowed).update(usersTable).start();

            Login_DoneActivity.this.startActivity(intent);
            Login_DoneActivity.this.finish();
        }
    }

}