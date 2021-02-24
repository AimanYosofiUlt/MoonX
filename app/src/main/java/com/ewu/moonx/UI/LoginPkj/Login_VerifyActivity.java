package com.ewu.moonx.UI.LoginPkj;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.processbutton.iml.ActionProcessButton;
import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.PublicVariable;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Template.OldAccount;
import com.ewu.moonx.Pojo.DB.Template.Str;
import com.ewu.moonx.Pojo.DB.Template.Users;
import com.ewu.moonx.Pojo.DB.UsersTable;
import com.ewu.moonx.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

public class Login_VerifyActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    String firstName, secondName, thirdName, phoneNumber;
    EditText codeED;
    ActionProcessButton verifyBtn;
    boolean isNeedConfig = true;
    int singedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verify);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        initEvent();
    }


    private void init() {
        mAuth = FirebaseAuth.getInstance();
        token = ((PhoneAuthProvider.ForceResendingToken) getIntent().getSerializableExtra(PublicVariable.TOKEN));
        verificationId = getIntent().getStringExtra(PublicVariable.VERIFICATION_ID);

        firstName = getIntent().getStringExtra(PublicVariable.FIRST_NAME);
        secondName = getIntent().getStringExtra(PublicVariable.SECOND_NAME);
        thirdName = getIntent().getStringExtra(PublicVariable.THIRD_NAME);
        phoneNumber = getIntent().getStringExtra(PublicVariable.PHONE);

        codeED = findViewById(R.id.codeED);
        verifyBtn = findViewById(R.id.verifyBtn);
        verifyBtn.setMode(ActionProcessButton.Mode.ENDLESS);
    }

    private void initEvent() {
        codeED.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkCounteryCode();
            }

            private void checkCounteryCode() {
                if (isCodeEntered()) {
                    codeED.setTextColor(Login_VerifyActivity.this.getResources().getColor(R.color.third_color));
                } else
                    codeED.setTextColor(Login_VerifyActivity.this.getResources().getColor(R.color.darkGray));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyBtn.getProgress() != PublicVariable.LOADING)
                    if (isNeedConfig)
                        configuration();
                    else
                        checkUserInFDB();
            }

            public void configuration() {
                boolean isNetConnected = Status.isNetConnected(Login_VerifyActivity.this), isCodeEntered = isCodeEntered();
                if (isNetConnected && isCodeEntered) {
                    verifyBtn.setMode(ActionProcessButton.Mode.ENDLESS);
                    codeED.setEnabled(false);
                    startConfig();
                } else if (!isNetConnected) {
                    Status.startShowBalloonMessage(Login_VerifyActivity.this, verifyBtn, getString(R.string.no_internet_connection));
                } else {
                    if (codeED.getText().toString().trim().equals(""))
                        Status.startShowBalloonMessage(Login_VerifyActivity.this, codeED, getString(R.string.empty_filed_error));
                    else
                        Status.startShowBalloonMessage(Login_VerifyActivity.this, codeED, getString(R.string.wrong_verification_code));
                }
            }


            private void startConfig() {
                final PhoneAuthCredential loginCredential = PhoneAuthProvider
                        .getCredential(verificationId, ((EditText) findViewById(R.id.codeED)).getText().toString());

                mAuth.signInWithCredential(loginCredential)
                        .addOnCompleteListener(Login_VerifyActivity.this, task -> {
                            if (task.isSuccessful()) {
                                checkUserInFDB();
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    showErrorMessage(getString(R.string.incorrect_verfication_code));
                                } else if (task.getException() instanceof FirebaseNetworkException) {
                                    showErrorMessage(getString(R.string.weak_internet_connection));
                                } else {
                                    showErrorMessage(Objects.requireNonNull(task.getException()).getMessage());
                                }
                            }
                        }).addOnFailureListener(e -> {
                    if (e instanceof FirebaseNetworkException) {
                        showErrorMessage(getString(R.string.weak_internet_connection));
                    } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Status.startShowBalloonMessage(Login_VerifyActivity.this, codeED, getString(R.string.wrong_verification_code));
                    } else {
                        showErrorMessage(e.getMessage());
                    }
                });
            }
        });
    }

    private void checkUserInFDB() {
        Firebase.FireCloudRef(Str.Users).document(PublicVariable.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    Users users = documentSnapshot.toObject(Users.class);
                    assert users != null;
                    singedCount = users.getSignCount();
                    sendRepeatedAccountNotify();
                } else {
                    singedCount = 0;
                    sendUserInfoToFDB();
                }
                Toast.makeText(Login_VerifyActivity.this, " Data is : " + singedCount, Toast.LENGTH_SHORT).show();
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorMessage(getString(R.string.weak_internet_connection));
                isNeedConfig = false;
            }
        });
    }

    private void sendRepeatedAccountNotify() {
        String id = singedCount + "-" + PublicVariable.getUid();
        OldAccount oldAccount = new OldAccount(PublicVariable.getUid(), Str.CMD_RepeatedAccount);
        Firebase.FireCloudRef(Str.OldAccount).document(id).set(oldAccount)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendUserInfoToFDB();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showErrorMessage(getString(R.string.weak_internet_connection));
                isNeedConfig = false;
            }
        });
    }

    private void sendUserInfoToFDB() {
        singedCount++;
        Users user = new Users(PublicVariable.getUid()
                , firstName
                , secondName
                , thirdName
                , phoneNumber
                , UsersTable.hisAdmin
                , singedCount);

        Firebase.FireCloudRef(Str.TempUsers).document(PublicVariable.getUid()).set(user)
                .addOnSuccessListener(onSuccess())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorMessage(getString(R.string.weak_internet_connection));
                        isNeedConfig = false;
                    }
                });
    }


    private OnSuccessListener<? super Void> onSuccess() {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                saveInfoInDB();
                openActivity();
            }

            private void saveInfoInDB() {
                UsersTable users = new UsersTable(Login_VerifyActivity.this);
                DB.insert(users.idCol, PublicVariable.getUid())
                        .insert(users.firstNameCol, firstName)
                        .insert(users.secondNameCol, secondName)
                        .insert(users.thirdNameCol, thirdName)
                        .insert(users.phoneCol, phoneNumber)
                        .insert(users.typeCol, UsersTable.hisAdmin)
                        .insert(users.signCountCol, singedCount)
                        .inTo(users);
            }

            private void openActivity() {
                Intent intent = new Intent(Login_VerifyActivity.this, Login_DoneActivity.class);
                startActivity(intent);
                Login_VerifyActivity.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        };
    }

    private boolean isCodeEntered() {
        return codeED.getText().toString().trim().length() == 6;
    }

    private void showErrorMessage(String message) {
        Status.showErrorMessage(this, verifyBtn, message);
        codeED.setEnabled(true);
    }
}