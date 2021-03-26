package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import com.ewu.moonx.App.CustomView;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.R;

public class ReplayView extends CustomView {
    String mesId, userName, msgStr;

    public ReplayView(Activity con, String mesId, String userName, String msgStr) {
        super(con, R.layout.view_replay_shower);
        this.mesId = mesId;
        this.userName = userName;
        this.msgStr = msgStr;

        ((TextView) _f(R.id.howUser)).setText(userName);
        ((TextView) _f(R.id.replayText)).setText(msgStr);
    }

    @Override
    protected void initEvent() {
        _f(R.id.theView).setOnClickListener(v -> {
            if (MsgBubbleView.getSelectedBubble().isEmpty()) {
                Intent intent = new Intent(con.getString(R.string.innerReplayBroadcast));
                intent.putExtra(Static.msgId, mesId);
                con.sendBroadcast(intent);
            }
        });
    }
}
