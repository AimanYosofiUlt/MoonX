package com.ewu.moonx.App;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.PublicChatService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Static {
    public static final int NORMAL = 0;
    public static final int LOADING = 1;
    public static final int ERROR = -1;

    public static final String VERIFICATION_ID = "VERIFICATION_ID";
    public static final String TOKEN = "TOKEN";

    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String SECOND_NAME = "SECOND_NAME";
    public static final String THIRD_NAME = "THIRD_NAME";
    public static final String PHONE = "PHONE";

    public static final String UserInfo = "UserInfo";

    public static final String isPublicMsg = "isPublicMsg";
    public static final String PublicMsg = "PublicMsg";
    public static final String UserName = "UserName";
    public static final String UserId = "UserId";

    public static final String UserType = "UserType";

    public static final String isForGetChatUser  = "isForGetChatUser";
    public static final String BubbleType = "BubbleType";
    public static final String msgId = "msgId";

    public static String getProfileImagePath(Context context) {
        return getMainPath(context) + "/Profile Image";
    }

    private static String getMainPath(Context context) {
        return Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name);
    }

    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(Date date) {
        return new SimpleDateFormat("hh:mm a").format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateTimeString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(date);
    }

    public static Date getDate(String string) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        return sdf.parse(string);
    }

    public static DatabaseReference getSendPublicRef(String userType, String userId)
    {
        if (userType.equals(UsersTable.hisEmpAdmin))
            return Firebase.RealTimeRef(Str.PublicMessages).child(Str.ForUsers).child(userId);
        else
            return Firebase.RealTimeRef(Str.PublicMessages).child(Str.ForAdmin);
    }
}
