package com.ewu.moonx.App;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PublicVariable {
    public static final int NORMAL =0;
    public static final int LOADING =1;
    public static final int ERROR =-1;

    public static final String VERIFICATION_ID = "VERIFICATION_ID";
    public static final String TOKEN = "TOKEN";

    public static final String FIRST_NAME = "FIRST_NAME" ;
    public static final String SECOND_NAME = "SECOND_NAME" ;
    public static final String THIRD_NAME = "THIRD_NAME" ;
    public static final String PHONE = "PHONE" ;

    public static final String UserInfo  = "UserInfo";

    public static String getUid(){
       return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

}
