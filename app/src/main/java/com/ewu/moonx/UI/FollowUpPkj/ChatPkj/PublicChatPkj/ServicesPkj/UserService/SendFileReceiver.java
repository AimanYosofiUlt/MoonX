package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.UserService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SendFileReceiver extends BroadcastReceiver {
    Context con;
    ArrayList<SendFileTracker> sendList;

    public SendFileReceiver(Context context) {
        this.con = context;
        sendList = new ArrayList<>();
        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void init() throws ParseException {

        MessageTable table = new MessageTable(con);
        Cursor needSendCursor = DB.selectAll()
                .from(table)
                .where(table.statueCol, MessageTable.ItsWaitContent)
                .start();

        if (needSendCursor.getCount() > 0) {
            while (needSendCursor.moveToNext()) {
                UserMessages message = new UserMessages();
                message.setId(needSendCursor.getString(0));
                message.setSenderUid(needSendCursor.getString(1));
                message.setReceiverUid(needSendCursor.getString(2));
                message.setText(needSendCursor.getString(3));
                message.setType(needSendCursor.getString(4));
                message.setDate(Static.getDate(needSendCursor.getString(5)));
                message.setReplayMsgId(needSendCursor.getString(6));

                sendList.add(new SendFileTracker(con, message));
                sendList();
            }
        }

        needSendCursor.close();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UserMessages message = ((UserMessages) intent.getSerializableExtra(Static.UserMsg));
        sendList.add(new SendFileTracker(con, message));

        Log.d(TAG, "onReceive: R2");
        sendList();
    }

    private void sendList() {
        Log.d("SendUserChatReciver", "sendList: R send list");
        for (SendFileTracker tracker : sendList) {
            if (tracker.getStatus() != SendPublicTracker.ON_PROGRESS) {
                Log.d("SendUserChatReciver", "sendList: R3 start send " + tracker.getMessage().getText());

                tracker.setStatus(SendPublicTracker.ON_PROGRESS);
                Uri file = Uri.fromFile(
                        getFile(tracker.getMessage().getId(), tracker.getMessage().getType())
                );

                UploadTask uploadTask = Firebase.StorageRef(Str.Users).child(Str.Files)
                        .child(tracker.getMessage().getReceiverUid())
                        .child(tracker.getMessage().getType())
                        .child(file.getLastPathSegment())
                        .putFile(file);


                tracker.setUploadTask(uploadTask);
            }
        }
    }

    private File getFile(String filename, String type) {
        if (type.equals(UserMessages.RECORD_TYPE))
            return Static.getRecordFile(con, filename);

        return Static.getRecordFile(con, filename);
    }

    private OnFailureListener onFailure(SendUserTracker sendPublicTracker) {
        return e -> {
            sendPublicTracker.setStatus(SendUserTracker.NOT_SENT);
        };
    }

    private OnSuccessListener<? super Void> onComplete(SendUserTracker sendPublicTracker) {
        return (OnSuccessListener<Void>) aVoid -> {
            sendList.remove(sendPublicTracker);
            Intent intent = new Intent(con.getString(R.string.done_SendUserMessagesBroadCast));
            intent.putExtra(Static.UserMsg, sendPublicTracker.getMessage());
            con.sendBroadcast(intent);
        };
    }

}
