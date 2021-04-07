package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.app.Activity;

import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.R;

public class ReceiverBubbleView extends MsgBubbleView {
    public ReceiverBubbleView(Activity con, Messages message) {
        super(con, R.layout.view_receiver_bubble, RECEIVER_BUBBLE, message);
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
