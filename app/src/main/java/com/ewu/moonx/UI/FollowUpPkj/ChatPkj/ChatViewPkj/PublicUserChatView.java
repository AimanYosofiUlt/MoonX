package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatViewPkj;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.Pojo.DB.Tables.SettingTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatActivity;

public class PublicUserChatView extends UserChatView {
    PublicMessages message;

    public PublicUserChatView(Activity con, PublicMessages message, String statue) {
        super(con, message.getUserId());
        this.con = con;
        this.message = message;
        setMessage(message, statue);
    }

    @Override
    protected void showChatActivity() {
        Intent intent = new Intent(con, ChatActivity.class);
        intent.putExtra(Static.isPublicMsg, true);
        intent.putExtra(Static.UserId, message.getUserId());
        intent.putExtra(Static.UserName, message.getUserName());
        intent.putExtra(Static.UserType, SettingTable.hisEmpAdmin);
        mesCount = 0;
        _f(R.id.mesCount).setVisibility(View.GONE);
        con.startActivity(intent);
    }

    @Override
    public void setCountVisibility() {
        PublicMessagesTable table = new PublicMessagesTable(con);
        Cursor cursor = DB.selectCount(table.statueCol).from(table)
                .where(table.statueCol, MessageTable.StUser_notRead).and.where(table.userIdCol, message.getUserId()).start();
        cursor.moveToNext();
        mesCount = cursor.getInt(0);
        ((TextView) _f(R.id.mesCount)).setText(String.valueOf(mesCount));

        if (mesCount > 0)
            _f(R.id.mesCount).setVisibility(View.VISIBLE);
        else
            _f(R.id.mesCount).setVisibility(View.GONE);

    }

    public void setMessage(PublicMessages message, String statue) {
        this.message = message;
        init(message.getUserName(), message.getText(), Static.getTime(message.getDate()), statue);
        _f(R.id.mesType).setVisibility(View.GONE);
    }

    public PublicMessages getMessage() {
        return message;
    }
}
