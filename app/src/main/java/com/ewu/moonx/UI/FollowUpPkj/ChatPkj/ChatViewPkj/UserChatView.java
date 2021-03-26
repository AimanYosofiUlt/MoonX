package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatViewPkj;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ewu.moonx.App.CustomView;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatFragment;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class UserChatView extends CustomView {
    String userId;
    int mesCount = 0;

    public UserChatView(Activity con, String userId) {
        super(con, R.layout.view_user_chat);
        this.userId = userId;
    }

    protected void initEvent() {
        _f(R.id.mainRL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment.currentPublicChatUser = getUserId();
                showChatActivity();
            }
        });
    }

    protected abstract void showChatActivity();

    public String getUserId() {
        return userId;
    }

    protected void init(String userName, String mesText, String time, String statue) {
        _t(R.id.userName).setText(userName);
        _t(R.id.message).setText(mesText);
        _t(R.id.time).setText(time);
        _t(R.id.mesCount).setVisibility(View.GONE);

        switch (statue) {
            case MessageTable.ItsInProgress:
                _i(R.id.mesStatue).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.timer));
                break;

            case MessageTable.ItsReceived:
                _i(R.id.mesStatue).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.msg_receive));
                break;

            case MessageTable.ItsReaded:
                _i(R.id.mesStatue).setImageDrawable(ContextCompat.getDrawable(con, R.drawable.msg_watch));
                break;

            case MessageTable.StUser_Read:
                _i(R.id.mesStatue).setVisibility(View.GONE);
                break;

            case MessageTable.StUser_notRead:
                _i(R.id.mesStatue).setVisibility(View.GONE);
                break;
        }


    }

    protected abstract void setCountVisibility();
}
