package com.ewu.moonx.UI.MainPkj;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.PublicVariable;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.Template.Str;
import com.ewu.moonx.Pojo.DB.Template.Users;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.LoginPkj.Login_VerifyActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();
    }

    private void init() {

    }

    private void initEvent() {
    }
}