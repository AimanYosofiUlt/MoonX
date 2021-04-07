package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.UserService;

import android.content.Context;
import android.content.Intent;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;
import com.ewu.moonx.R;
import com.google.firebase.storage.UploadTask;

public class SendFileTracker extends SendUserTracker {
    UploadTask uploadTask;
    Context con;

    public SendFileTracker(Context con, UserMessages message) {
        super(message);
        this.con = con;
    }

    public void setUploadTask(UploadTask uploadTask) {
        this.uploadTask = uploadTask;

        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Intent intent = new Intent(con.getString(R.string.inProgress_FileBroadcast));
            intent.putExtra(Static.UserMsg, message);
            intent.putExtra(Static.Progress, progress);
            con.sendBroadcast(intent);
        }).addOnSuccessListener(taskSnapshot -> {
            Intent intent = new Intent(con.getString(R.string.done_SendFileBroadcast));
            intent.putExtra(Static.UserMsg, message);
            con.sendBroadcast(intent);
        }).addOnFailureListener(e -> {
            Intent intent = new Intent(con.getString(R.string.stop_SendFileBroadcast));
            intent.putExtra(Static.UserMsg, message);
            con.sendBroadcast(intent);
        }).addOnPausedListener(snapshot -> {
            Intent intent = new Intent(con.getString(R.string.stop_SendFileBroadcast));
            intent.putExtra(Static.UserMsg, message);
            con.sendBroadcast(intent);
        });
    }
}
