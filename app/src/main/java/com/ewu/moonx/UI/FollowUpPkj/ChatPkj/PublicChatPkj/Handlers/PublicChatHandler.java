package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.Handlers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DBOrder;
import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.MsgBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.ReplayView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.SenderBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_Content;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_TextContent;

import java.text.ParseException;
import java.util.Calendar;

public class PublicChatHandler extends ChatHandler {
    String userId, userName;
    String userType;
    BroadcastReceiver receivePublicMessageBroadCast, sendPublicMessagesDoneBroadCast;

    public PublicChatHandler(Activity con, String userId, String userName, String userType) {
        super(con);
        this.userId = userId;
        this.userName = userName;
        this.userType = userType;
        initBroadcastReceivers();
    }

    @Override
    public void initData() {
        PublicMessagesTable table = new PublicMessagesTable(con);
        chatCursor = DB.selectAll().from(table).where(table.userIdCol, userId).orderBy(table.dateCol, DBOrder.DESC).start();
        addFromDB();
    }

    @Override
    protected String getStatueFromChatCursor() {
        return chatCursor.getString(5);
    }

    @Override
    protected void initBroadcastReceivers() {
        super.initBroadcastReceivers();

        sendPublicMessagesDoneBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Messages message = (Messages) intent.getSerializableExtra(Static.PublicMsg);
                makeSendBubbleDone(message);
            }
        };

        receivePublicMessageBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PublicMessages message = (PublicMessages) intent.getSerializableExtra(Static.PublicMsg);
                if (message.getUserId().equals(userId)) {
                    receiveBubble(message);

                    PublicMessagesTable table = new PublicMessagesTable(context);
                    DB.set(table.statueCol, MessageTable.StUser_Read).update(table).where(table.idCol, message.getId()).exec();
                }
            }
        };

        con.registerReceiver(sendPublicMessagesDoneBroadCast, new IntentFilter(con.getString(R.string.done_SendPublicMessagesDoneBroadCast)));
        con.registerReceiver(receivePublicMessageBroadCast, new IntentFilter(con.getString(R.string.receive_PublicMessageBroadCast)));
    }


    @Override
    protected void deleteFromDB(Messages message) {
        PublicMessagesTable table = new PublicMessagesTable(con);
        DB.delete(table).where(table.idCol, message.getId()).exec();
    }

    @Override
    protected BM_Content getReceiveContent(Messages message) {
        return new BM_TextContent(con, message.getText());
    }

    @Override
    public boolean isPublic() {
        return true;
    }


    @Override
    protected void makeMessageAsReaded(Messages message) {
        PublicMessagesTable table = new PublicMessagesTable(con);
        DB.set(table.statueCol, MessageTable.StUser_Read).update(table).where(table.idCol, message.getId()).exec();
    }


    @Override
    protected Messages getMessage() {
        PublicMessages message = new PublicMessages();
        message.setId(chatCursor.getString(0));
        message.setUserId(chatCursor.getString(1));
        message.setUserName(chatCursor.getString(2));
        message.setText(chatCursor.getString(3));
        try {
            message.setDate(Static.getDate(chatCursor.getString(4)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        message.setReplayMsgId(chatCursor.getString(6));
        return message;
    }

    @Override
    public void sendTextMessage(String msgStr) {
        String mesId = Static.getSendPublicRef(userType, userId).push().getKey();

        PublicMessages message = new PublicMessages(mesId, userId, userName, msgStr, Calendar.getInstance().getTime(), null);
        if (replayMsg != null) message.setReplayMsgId(replayMsg.getId());

        SenderBubbleView bubble = new SenderBubbleView(con, message, MessageTable.ItsInProgress);
        bubble.setContent(new BM_TextContent(con, bubble.getMessage().getText()));

        if (replayMsg != null)
            addMessageReplay(bubble);

        Firebase.addSenderPublicMessageToDB(con, message);

        sendUserMessageBroadcast(message);
        addSendedMessages(bubble);
    }

    @Override
    protected void addMessageReplay(MsgBubbleView bubble) {
        PublicMessagesTable table = new PublicMessagesTable(con);
        Cursor cursor = DB.select(table.idCol)
                .select(table.userNameCol)
                .select(table.statueCol)
                .select(table.textCol)
                .from(table).where(table.idCol, bubble.getMessage().getReplayMsgId()).start();

        if (cursor.getCount() > 0) {
            cursor.moveToNext();

            ReplayView replayView = new ReplayView(con, cursor.getString(0)
                    , ((cursor.getString(2).contains("Its"))) ? con.getString(R.string.You) : cursor.getString(1)
                    , cursor.getString(3));

            bubble.addReplayView(replayView);
        }

        cursor.close();
    }

    @Override
    protected boolean itsReceiverMessages(Cursor chatCursor) {
        return chatCursor.getString(5).contains("St");
    }

    private void sendUserMessageBroadcast(PublicMessages message) {
        Intent intent = new Intent(con.getString(R.string.send_PublicMessageBroadCast));
        intent.putExtra(Static.PublicMsg, message);
        con.sendBroadcast(intent);
    }

    @Override
    public void clear() {
        super.clear();
        con.unregisterReceiver(sendPublicMessagesDoneBroadCast);
        con.unregisterReceiver(receivePublicMessageBroadCast);
    }
}
