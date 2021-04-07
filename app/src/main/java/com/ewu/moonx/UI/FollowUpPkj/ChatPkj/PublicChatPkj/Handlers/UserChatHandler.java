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
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Models.Messages;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.MsgBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.ReplayView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj.SenderBubbleView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_Content;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_SoundContent;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.MsgContentPkj.BM_TextContent;

import java.text.ParseException;
import java.util.Calendar;

public class UserChatHandler extends ChatHandler {
    Users chatUser;
    BroadcastReceiver receiveUserMessageBroadCast, sendUserMessagesDoneBroadCast;
    BroadcastReceiver inProgress_FileBroadcast, done_SendFileBroadcast, stop_SendFileBroadcast;

    public UserChatHandler(Activity con, Users chatUser) {
        super(con);
        this.chatUser = chatUser;
    }

    public Users getChatUser() {
        return chatUser;
    }

    @Override
    protected void initBroadcastReceivers() {
        super.initBroadcastReceivers();

        sendUserMessagesDoneBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Messages message = (Messages) intent.getSerializableExtra(Static.UserMsg);
                makeSendBubbleDone(message);
            }
        };

        receiveUserMessageBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UserMessages message = (UserMessages) intent.getSerializableExtra(Static.UserMsg);
                if (message.getSenderUid().equals(chatUser.getId())) {
                    receiveBubble(message);
                    MessageTable table = new MessageTable(context);
                    DB.set(table.statueCol, MessageTable.StUser_Read).update(table).where(table.idCol, message.getId()).exec();
                }
            }
        };

        inProgress_FileBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UserMessages message = (UserMessages) intent.getSerializableExtra(Static.UserMsg);
                double progress = intent.getDoubleExtra(Static.Progress, 0);

                for (SenderBubbleView needSendedView : needSendedViews) {
                    if (needSendedView.getMessage().getId().equals(message.getId())) {
                        BM_Content content = needSendedView.getContent();
                        content.setProgress(progress);
                    }
                }
            }
        };

        done_SendFileBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UserMessages message = (UserMessages) intent.getSerializableExtra(Static.UserMsg);

                for (SenderBubbleView needSendedView : needSendedViews) {
                    if (needSendedView.getMessage().getId().equals(message.getId())) {
                        BM_Content content = needSendedView.getContent();
                        content.done();
                    }
                }
            }
        };

        stop_SendFileBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };


        con.registerReceiver(done_SendFileBroadcast, new IntentFilter(con.getString(R.string.done_SendFileBroadcast)));
        con.registerReceiver(stop_SendFileBroadcast, new IntentFilter(con.getString(R.string.stop_SendFileBroadcast)));
        con.registerReceiver(inProgress_FileBroadcast, new IntentFilter(con.getString(R.string.inProgress_FileBroadcast)));
        con.registerReceiver(sendUserMessagesDoneBroadCast, new IntentFilter(con.getString(R.string.done_SendUserMessagesBroadCast)));
        con.registerReceiver(receiveUserMessageBroadCast, new IntentFilter(con.getString(R.string.receive_UserMessageBroadCast)));
    }

    @Override
    public void sendTextMessage(String msgStr) {
        String mesId = getMesId();
        SenderBubbleView bubble = propareMessage(mesId, msgStr, UserMessages.TEXT_TYPE);
        bubble.setContent(new BM_TextContent(con, bubble.getMessage().getText()));
        sendUserMessageBroadcast(((UserMessages) bubble.getMessage()));
    }

    private SenderBubbleView propareMessage(String mesId, String msgStr, String type) {
        UserMessages message = new UserMessages(mesId, Static.getUid(), chatUser.getId(), msgStr, type, Calendar.getInstance().getTime(), null);
        if (replayMsg != null)
            message.setReplayMsgId(replayMsg.getId());

        SenderBubbleView bubble = new SenderBubbleView(con, message, MessageTable.ItsInProgress);

        if (replayMsg != null)
            addMessageReplay(bubble);

        Firebase.addSenderUserMessageToDB(con, message);
        addSendedMessages(bubble);
        return bubble;
    }

    private void sendUserMessageBroadcast(UserMessages message) {
        Intent intent = new Intent(con.getString(R.string.send_UserMessageBroadCast));
        intent.putExtra(Static.UserMsg, message);
        con.sendBroadcast(intent);
    }

    @Override
    protected void deleteFromDB(Messages message) {

    }

    @Override
    protected BM_Content getReceiveContent(Messages message) {
        if (((UserMessages) message).getType().equals(UserMessages.TEXT_TYPE))
            return new BM_TextContent(con, message.getText());
        else
            return new BM_TextContent(con, message.getText());
    }

    @Override
    public boolean isPublic() {
        return false;
    }

    @Override
    public void initData() {
        MessageTable table = new MessageTable(con);
        chatCursor = DB.selectAll().from(table)
                .where(table.receiverUidCol, chatUser.getId()).or.where(table.senderUidCol, chatUser.getId())
                .orderBy(table.dateCol, DBOrder.DESC).start();

        addFromDB();
    }

    @Override
    protected void addMessageReplay(MsgBubbleView bubble) {
        MessageTable table = new MessageTable(con);
        Cursor cursor = DB.select(table.idCol)
                .select(table.statueCol)
                .select(table.contentCol)
                .from(table).where(table.idCol, bubble.getMessage().getReplayMsgId()).start();

        if (cursor.getCount() > 0) {
            cursor.moveToNext();

            ReplayView replayView = new ReplayView(con, cursor.getString(0)
                    , ((cursor.getString(1).contains("Its"))) ? con.getString(R.string.You) : chatUser.getFirstName() + " " + chatUser.getThirdName()
                    , cursor.getString(2));

            bubble.addReplayView(replayView);
        }

        cursor.close();
    }

    @Override
    protected boolean itsReceiverMessages(Cursor chatCursor) {
        return chatCursor.getString(2).equals(Static.getUid());
    }

    @Override
    protected String getStatueFromChatCursor() {
        return chatCursor.getString(6);
    }

    @Override
    protected Messages getMessage() {
        UserMessages message = new UserMessages();
        message.setId(chatCursor.getString(0));
        message.setSenderUid(chatCursor.getString(1));
        message.setReceiverUid(chatCursor.getString(2));
        message.setText(chatCursor.getString(3));
        message.setType(chatCursor.getString(4));
        try {
            message.setDate(Static.getDate(chatCursor.getString(5)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        message.setReplayMsgId(chatCursor.getString(7));
        return message;
    }

    @Override
    protected void makeMessageAsReaded(Messages message) {
        MessageTable table = new MessageTable(con);
        DB.set(table.statueCol, MessageTable.StUser_Read).update(table).where(table.idCol, message.getId()).exec();
    }

    @Override
    public void clear() {
        super.clear();
        con.unregisterReceiver(sendUserMessagesDoneBroadCast);
        con.unregisterReceiver(receiveUserMessageBroadCast);
        con.unregisterReceiver(inProgress_FileBroadcast);
        con.unregisterReceiver(stop_SendFileBroadcast);
        con.unregisterReceiver(done_SendFileBroadcast);
    }

    public String getMesId() {
        return Firebase.RealTimeRef(Str.Messages).child(chatUser.getId()).push().getKey();
    }

    public void sendRecord(String mesId, String recordTime) {
        SenderBubbleView bubble = propareMessage(mesId, recordTime, UserMessages.RECORD_TYPE);

        BM_SoundContent soundContent = new BM_SoundContent(con, mesId);
        soundContent.setAsSender();
        bubble.setContent(soundContent);

        sendFileBroadcast(((UserMessages) bubble.getMessage()));
    }

    private void sendFileBroadcast(UserMessages message) {
        Intent intent = new Intent(con.getString(R.string.send_FileBroadcast));
        intent.putExtra(Static.UserMsg, message);
        con.sendBroadcast(intent);
    }
}
