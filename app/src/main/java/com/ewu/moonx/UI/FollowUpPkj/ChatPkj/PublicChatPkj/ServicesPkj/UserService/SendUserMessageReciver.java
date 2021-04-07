package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.UserService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.PublicService.SendPublicTracker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SendUserMessageReciver extends BroadcastReceiver {
    Context context;
    ArrayList<SendUserTracker> sendList;

    public SendUserMessageReciver(Context context) {
        this.context = context;
        sendList = new ArrayList<>();
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void init() throws ParseException {
        MessageTable table = new MessageTable(context);

        Log.d("SendUserChatReciver", "init: R1");
        Cursor cursor = DB.selectAll().from(table).where(table.statueCol, MessageTable.ItsInProgress).start();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Log.d("SendUserChatReciver", "init: R2 " + cursor.getString(3));
                UserMessages message = new UserMessages();
                message.setId(cursor.getString(0));
                message.setSenderUid(cursor.getString(1));
                message.setReceiverUid(cursor.getString(2));
                message.setText(cursor.getString(3));
                message.setType(cursor.getString(4));
                message.setDate(Static.getDate(cursor.getString(5)));
                message.setReplayMsgId(cursor.getString(7));

                sendList.add(new SendUserTracker(message));
                sendList();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UserMessages message = ((UserMessages) intent.getSerializableExtra(Static.UserMsg));
        sendList.add(new SendUserTracker(message));

        Log.d(TAG, "onReceive: R2");
        sendList();
    }

    private void sendList() {
        Log.d("SendUserChatReciver", "sendList: R send list");
        for (SendUserTracker tracker : sendList) {
            if (tracker.getStatus() != SendPublicTracker.ON_PROGRESS) {
                Log.d("SendUserChatReciver", "sendList: R3 start send " + tracker.getMessage().getText());

                tracker.setStatus(SendPublicTracker.ON_PROGRESS);

                Firebase.RealTimeRef(Str.Messages)
                        .child(tracker.getMessage().getReceiverUid())
                        .child(tracker.getMessage().getId())
                        .setValue(tracker.getMessage())
                        .addOnSuccessListener(onComplete(tracker))
                        .addOnFailureListener(onFailure(tracker));

            }
        }
    }

    private OnFailureListener onFailure(SendUserTracker sendPublicTracker) {
        return e -> {
            sendPublicTracker.setStatus(SendUserTracker.NOT_SENT);
        };
    }

    private OnSuccessListener<? super Void> onComplete(SendUserTracker sendPublicTracker) {
        return (OnSuccessListener<Void>) aVoid -> {
            sendList.remove(sendPublicTracker);
            Intent intent = new Intent(context.getString(R.string.done_SendUserMessagesBroadCast));
            intent.putExtra(Static.UserMsg, sendPublicTracker.getMessage());
            context.sendBroadcast(intent);
        };
    }
}
