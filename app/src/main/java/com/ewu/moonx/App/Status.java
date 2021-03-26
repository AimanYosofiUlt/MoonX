package com.ewu.moonx.App;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.dd.processbutton.iml.ActionProcessButton;
import com.ewu.moonx.R;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

public class Status {
    public static boolean isNetConnected(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public static void startShowBalloonMessage(Context context, View filed, String message) {
        Balloon balloon = new Balloon.Builder(context)
                .setArrowSize(10)
                .setArrowOrientation(ArrowOrientation.TOP)
                .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                .setArrowPosition(0.5f)
                .setWidth(BalloonSizeSpec.WRAP)
                .setHeight(65)
                .setTextSize(15f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText(message)
                .setTextColor(ContextCompat.getColor(context, R.color.white))
                .setTextIsHtml(true)
                .setPaddingRight(10)
                .setPaddingLeft(10)
                .setBackgroundColor(ContextCompat.getColor(context, R.color.red_error))
                .setBalloonAnimation(BalloonAnimation.ELASTIC)
                .build();

        balloon.show(filed);
    }

    public static void showErrorMessage(Context context, ActionProcessButton button, String message) {
        startShowBalloonMessage(context, button, context.getString(R.string.try_again));
        button.setErrorText(message);
        button.setProgress(Static.ERROR);
    }
}
