package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.app.Activity;

import com.ewu.moonx.R;

import java.util.Date;

public class DateInfoBubbleView extends BubbleView {
    public DateInfoBubbleView(Activity con, Date date) {
        super(con, R.layout.tools_date_bubble, BubbleView.DATE_BUBBLE);
        setDateStr(date);
        _t(R.id.date).setText(dateStr);
    }

    @Override
    protected void initEvent() {
    }
}
