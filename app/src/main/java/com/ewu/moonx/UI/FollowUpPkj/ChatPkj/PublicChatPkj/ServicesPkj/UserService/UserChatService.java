package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.ServicesPkj.UserService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class UserChatService extends Service {
    public static boolean isRunning = false;

    HandlerThread serviceThread;
    SendUserMessageReciver send_UserMessageBroadCast;
    SendFileReceiver send_FileBroadCast;
    private BroadcastReceiver sendUserMessagesDoneBroadCast;

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        send_UserMessageBroadCast = new SendUserMessageReciver(this);
        send_FileBroadCast = new SendFileReceiver(this);
        serviceThread = new HandlerThread("UserChatService");
        serviceThread.start();

        new Handler(serviceThread.getLooper()).post(
                () -> UserChatService.this.registerReceiver(send_UserMessageBroadCast, new IntentFilter(getString(R.string.send_UserMessageBroadCast))));

        new Handler(serviceThread.getLooper()).post(
                () -> UserChatService.this.registerReceiver(send_FileBroadCast, new IntentFilter(getString(R.string.send_FileBroadcast))));

        sendUserMessagesDoneBroadCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                MessageTable table = new MessageTable(context);
                UserMessages message = (UserMessages) intent.getSerializableExtra(Static.UserMsg);
                DB.set(table.statueCol, MessageTable.ItsSent).update(table).where(table.idCol, message.getId()).exec();
            }
        };
        registerReceiver(sendUserMessagesDoneBroadCast, new IntentFilter(getString(R.string.done_SendUserMessagesBroadCast)));

        initFireBase();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initFireBase() {
        Firebase.RealTimeRef(Str.Messages).child(Static.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handleReceiveMessage(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handleReceiveMessage(DataSnapshot dataSnapshot) {
        UserMessages message = dataSnapshot.getValue(UserMessages.class);
        sendReciveMessageBroadcast(message);
        Firebase.addReceiveUserMessageToDB(UserChatService.this, message);
        dataSnapshot.getRef().removeValue();
    }

    private void sendReciveMessageBroadcast(UserMessages message) {
        Intent intent = new Intent(getString(R.string.receive_UserMessageBroadCast));
        intent.putExtra(Static.UserMsg, message);
        sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        unregisterReceiver(send_UserMessageBroadCast);
        unregisterReceiver(sendUserMessagesDoneBroadCast);
        unregisterReceiver(send_FileBroadCast);
        serviceThread.quit();
    }
}
