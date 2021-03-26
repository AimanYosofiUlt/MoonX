package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.R;

public class ReceiverBubbleView extends MsgBubbleView {
    public ReceiverBubbleView(Activity con, PublicMessages publicMessage) {
        super(con, R.layout.view_receiver_bubble, RECEIVER_BUBBLE, publicMessage);
    }

    @Override
    protected boolean isInBoundery(float xValue, float firstValue) {
        return xValue > this.firstValue;
    }

    @Override
    protected float getReplayValue(float xValue, float firstValue) {
        return (xValue - firstValue) / 150;
    }
}
