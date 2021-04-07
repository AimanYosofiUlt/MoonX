package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatViewPkj;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatActivity;

import java.util.Formatter;

public class AdminUserChatView extends UserChatView {
    Users user;

    public AdminUserChatView(Activity con, Users user, UserMessages message) {
        super(con, user.getId());
        this.user = user;

        UsersTable usersTable = new UsersTable(con);
        _t(R.id.userName).setText(String.format("%s %s %s", user.getFirstName(), user.getSecondName(), user.getThirdName()));
    }

    @Override
    protected void showChatActivity() {
        Intent intent = new Intent(con, ChatActivity.class);
        intent.putExtra(Static.isPublicMsg, false);
        intent.putExtra(Static.UserInfo, user);
        mesCount = 0;
        _f(R.id.mesCount).setVisibility(View.GONE);
        con.startActivity(intent);
    }

    @Override
    protected void setCountVisibility() {
        MessageTable table = new MessageTable(con);

        Cursor cursor = DB.selectCount(table.statueCol).from(table)
                .where(table.statueCol, MessageTable.StUser_notRead).and.where(table.senderUidCol, userId).or.where(table.receiverUidCol, userId).start();

        cursor.moveToNext();
        mesCount = cursor.getInt(0);
        ((TextView) _f(R.id.mesCount)).setText(String.valueOf(mesCount));

        if (mesCount > 0)
            _f(R.id.mesCount).setVisibility(View.VISIBLE);
        else
            _f(R.id.mesCount).setVisibility(View.GONE);
    }
}
