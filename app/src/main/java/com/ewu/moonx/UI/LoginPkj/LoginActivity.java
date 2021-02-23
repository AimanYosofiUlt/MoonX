package com.ewu.moonx.UI.LoginPkj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dd.processbutton.iml.ActionProcessButton;
import com.ewu.moonx.App.PublicVariable;
import com.ewu.moonx.App.Status;
import com.ewu.moonx.R;
import com.ewu.moonx.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    ActionProcessButton loginBtn;
    EditText firstNameED, secondNameED, thirdNameED, phone;
    ImageButton countryBtn;
    int d = 0;

    private final String YEMEN = "+967";
    private final String EGYPT = "+2";
    String countryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init();
        initEvent();
    }

    private void init() {
        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setMode(ActionProcessButton.Mode.ENDLESS);
        firstNameED = findViewById(R.id.firstNameED);
        secondNameED = findViewById(R.id.secondNameED);
        thirdNameED = findViewById(R.id.thirdNameED);
        phone = findViewById(R.id.phoneED);
        countryBtn = findViewById(R.id.countryImgBtn);

        countryCode = YEMEN;
        registerForContextMenu(countryBtn);


    }

    private void initEvent() {
        countryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCounteryMenu();
            }

            private void showCounteryMenu() {
                PopupMenu popupMenu = new PopupMenu(LoginActivity.this, countryBtn);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.yemen:
                                countryBtn.setImageResource(R.drawable.yemen);
                                countryCode = YEMEN;
                                break;

                            case R.id.egypt:
                                countryBtn.setImageResource(R.drawable.egypt);
                                countryCode = EGYPT;
                                break;
                        }
                        checkCounteryCode();
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.counrty_menu);
                popupMenu.show();
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkCounteryCode();
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginBtn.getProgress() != PublicVariable.LOADING) {

                    setStateInLoading(PublicVariable.NORMAL);

                    boolean isNetConnected = Status.isNetConnected(LoginActivity.this), isAllInfoEntered = isAllInfoEntered();

                    if (isNetConnected && isAllInfoEntered) {
                        setStateInLoading(PublicVariable.LOADING);
                        VerifyPhone();
                    } else {
                        if (!isAllInfoEntered)
                            showBalloonMessage();
                        else {
                            Status.showErrorMessage(LoginActivity.this, loginBtn, getString(R.string.no_internet_connection));
                        }
                    }
                }
            }

            private void showBalloonMessage() {
                if (firstNameED.getText().toString().trim().equals(""))
                    Status.startShowBalloonMessage(LoginActivity.this, firstNameED, getString(R.string.empty_filed_error));
                else if (secondNameED.getText().toString().trim().equals(""))
                    Status.startShowBalloonMessage(LoginActivity.this, secondNameED, getString(R.string.empty_filed_error));
                else if (thirdNameED.getText().toString().trim().equals(""))
                    Status.startShowBalloonMessage(LoginActivity.this, thirdNameED, getString(R.string.empty_filed_error));
                else if (phone.getText().toString().trim().equals(""))
                    Status.startShowBalloonMessage(LoginActivity.this, phone, getString(R.string.empty_filed_error));
                else if (!isCounteryCodeEnabled()) {
                    Status.startShowBalloonMessage(LoginActivity.this, phone, getString(R.string.uncorrect_phone));
                    Status.startShowBalloonMessage(LoginActivity.this, countryBtn, getString(R.string.check));
                }
            }


            private boolean isAllInfoEntered() {
                return !(firstNameED.getText().toString().trim().equals("")
                        || secondNameED.getText().toString().trim().equals("")
                        || thirdNameED.getText().toString().trim().equals("")
                        || phone.getText().toString().trim().equals("")
                        || !isCounteryCodeEnabled());
            }


            private void setStateInLoading(int state) {
                loginBtn.setProgress(state);

                if (state == PublicVariable.LOADING) {
                    firstNameED.setEnabled(false);
                    secondNameED.setEnabled(false);
                    thirdNameED.setEnabled(false);
                    phone.setEnabled(false);
                } else {
                    firstNameED.setEnabled(true);
                    secondNameED.setEnabled(true);
                    thirdNameED.setEnabled(true);
                    phone.setEnabled(true);
                }


            }

            private void VerifyPhone() {
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                .setPhoneNumber(getPhoneNumber())       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(getCallBack())          // OnVerificationStateChangedCallbacks
                                .build();

                PhoneAuthProvider.verifyPhoneNumber(options);
            }


            private PhoneAuthProvider.OnVerificationStateChangedCallbacks getCallBack() {
                return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        HandelFireBaseException(e);
                    }

                    private void HandelFireBaseException(FirebaseException e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Status.showErrorMessage(LoginActivity.this, loginBtn, getString(R.string.invlaid_Auth));
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            Status.showErrorMessage(LoginActivity.this, loginBtn, getString(R.string.manyRequestExption));
                        } else {
                            Status.showErrorMessage(LoginActivity.this, loginBtn, getString(R.string.weak_internet_connection));
                        }
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Intent AuthIntent = new Intent(LoginActivity.this, Login_VerifyActivity.class);

                        AuthIntent.putExtra(PublicVariable.VERIFICATION_ID, verificationId);
                        AuthIntent.putExtra(PublicVariable.TOKEN, token);
                        AuthIntent.putExtra(PublicVariable.FIRST_NAME, firstNameED.getText().toString().trim());
                        AuthIntent.putExtra(PublicVariable.SECOND_NAME, secondNameED.getText().toString().trim());
                        AuthIntent.putExtra(PublicVariable.THIRD_NAME, thirdNameED.getText().toString().trim());
                        AuthIntent.putExtra(PublicVariable.PHONE, getPhoneNumber());

                        LoginActivity.this.startActivity(AuthIntent);
                        LoginActivity.this.finish();
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        super.onCodeAutoRetrievalTimeOut(s);
                    }


                };

            }

            private String getPhoneNumber() {
                return countryCode + ((EditText) findViewById(R.id.phoneED)).getText().toString();
            }
        });
    }

    private void checkCounteryCode() {
        if (isCounteryCodeEnabled())
            phone.setTextColor(LoginActivity.this.getResources().getColor(R.color.third_color));
        else
            phone.setTextColor(LoginActivity.this.getResources().getColor(R.color.darkGray));
    }

    private boolean isCounteryCodeEnabled() {
        int maxLength = 9;
        if (countryCode.equals(EGYPT))
            maxLength = 11;

        return phone.getText().toString().length() == maxLength;
    }
}