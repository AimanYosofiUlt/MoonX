package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.app.Activity;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.R;

public class SenderBubbleView extends MsgBubbleView {
    public SenderBubbleView(Activity con, Messages message, String statue) {
        super(con, R.layout.view_sender_bubble, SENDER_BUBBLE, message);
        seStatue(statue);
    }

    public void seStatue(String statue) {
        switch (statue) {
            case MessageTable.ItsInProgress:
            case MessageTable.ItsWaitContent:
                ((ImageView) _f(R.id.msgStatue)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.msg_timer));
                break;

            case MessageTable.ItsSent:
                ((ImageView) _f(R.id.msgStatue)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.msg_send));
                break;

            case MessageTable.ItsReaded:
                ((ImageView) _f(R.id.msgStatue)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.msg_watch));
                break;

            case MessageTable.ItsReceived:
                ((ImageView) _f(R.id.msgStatue)).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.msg_receive));
                break;
        }
    }

    @Override
    protected boolean isInBoundery(float xValue, float firstValue) {
        return xValue < this.firstValue;
    }

    @Override
    protected float getReplayValue(float xValue, float firstValue) {
        return (firstValue - xValue) / 150;
    }

}
