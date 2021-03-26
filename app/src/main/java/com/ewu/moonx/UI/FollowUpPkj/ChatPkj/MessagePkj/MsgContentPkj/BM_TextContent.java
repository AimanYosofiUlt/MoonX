package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj;

import android.app.Activity;
import android.widget.TextView;

import com.ewu.moonx.R;

public class BM_TextContent extends BM_Content {

    public BM_TextContent(Activity con, String text) {
        super(con, R.layout.view_text_content);
        _t(R.id.text).setText(text);
    }

    @Override
    protected void initEvent() {

    }
}
