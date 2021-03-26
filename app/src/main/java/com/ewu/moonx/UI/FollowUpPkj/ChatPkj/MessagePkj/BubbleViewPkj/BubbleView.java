package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.app.Activity;

import com.ewu.moonx.App.CustomView;
import com.ewu.moonx.App.Static;

import java.util.Date;

public abstract class BubbleView extends CustomView {
    public static final short DATE_BUBBLE = 0;
    public static final short SENDER_BUBBLE  =1;
    public static final short RECEIVER_BUBBLE = 2;

    String dateStr;
    short bubbleType;

    public BubbleView(Activity con, int R_layout, short bubbleType) {
        super(con, R_layout);
        this.bubbleType = bubbleType;
    }
    public short getBubbleType() {
        return bubbleType;
    }

    protected void setDateStr(Date date) {
        this.dateStr = Static.getDate(date);
    }

    public String getDate() {
        return dateStr;
    }
}
