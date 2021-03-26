package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SendPublicChatReciver extends BroadcastReceiver {
    Context context;
    ArrayList<SendPublicTracker> sendList;

    public SendPublicChatReciver(Context context) {
        this.context = context;
        sendList = new ArrayList<>();
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void init() throws ParseException {
        PublicMessagesTable table = new PublicMessagesTable(context);
        Cursor cursor = DB.selectAll().from(table).where(table.statueCol, MessageTable.ItsInProgress).start();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                PublicMessages message = new PublicMessages(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        Static.getDate(cursor.getString(4)),
                        cursor.getString(6)
                );

                sendList.add(new SendPublicTracker(message));
                sendList();
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PublicMessages message = ((PublicMessages) intent.getSerializableExtra(Static.PublicMsg));
        sendList.add(new SendPublicTracker(message));
        Toast.makeText(context, "on send reciver", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "onReceive: on Send Receive");
        sendList();
    }

    private void sendList() {
        for (SendPublicTracker sendPublicTracker : sendList) {
            if (sendPublicTracker.getStatus() != SendPublicTracker.ON_PROGRESS) {
                sendPublicTracker.setStatus(SendPublicTracker.ON_PROGRESS);

                DatabaseReference reference = Static.getSendPublicRef(PublicChatService.userType, sendPublicTracker.getMessage().getUserId());

                reference.child(sendPublicTracker.getMessage().getId()).setValue(sendPublicTracker.getMessage())
                        .addOnSuccessListener(onComplete(sendPublicTracker))
                        .addOnFailureListener(onFailure(sendPublicTracker));

            }
        }
    }

    private OnFailureListener onFailure(SendPublicTracker sendPublicTracker) {
        return e -> {
            sendPublicTracker.setStatus(SendPublicTracker.NOT_SENT);
        };
    }

    private OnSuccessListener<? super Void> onComplete(SendPublicTracker sendPublicTracker) {
        return (OnSuccessListener<Void>) aVoid -> {
            sendList.remove(sendPublicTracker);
            Intent intent = new Intent(context.getString(R.string.sendDone_PublicMessageBroadCast));
            intent.putExtra(Static.PublicMsg, sendPublicTracker.getMessage());
            context.sendBroadcast(intent);
        };
    }
}
