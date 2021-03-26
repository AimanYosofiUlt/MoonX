package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj;

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
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class PublicChatService extends Service {
    public static boolean isRunning = false;
    HandlerThread serviceThread;
    public static String userId;
    public static String userType;
    SendPublicChatReciver receiver;
    String TAG = "PublicChatService";

    @Override
    public void onCreate() {
        isRunning = false;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: onStartCommand");

        isRunning = true;
        userId = intent.getStringExtra(Static.UserId);
        userType = intent.getStringExtra(Static.UserType);

        receiver = new SendPublicChatReciver(this);
        serviceThread = new HandlerThread("PublicChatService");
        serviceThread.start();
        new Handler(serviceThread.getLooper()).post(() -> {
            Log.d(TAG, "run: registerReceiver");
            PublicChatService.this.registerReceiver(receiver, new IntentFilter(getString(R.string.send_PublicMessageBroadCast)));
        });
        initFireBase();

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PublicMessagesTable table = new PublicMessagesTable(context);
                PublicMessages message = (PublicMessages) intent.getSerializableExtra(Static.PublicMsg);
                DB.set(table.statueCol,MessageTable.ItsSent).update(table).where(table.idCol,message.getId()).exec();

            }
        }, new IntentFilter(getString(R.string.sendDone_PublicMessageBroadCast)));
        return START_STICKY;
    }

    private void initFireBase() {
        DatabaseReference reference = null;
        if (PublicChatService.userType.equals(UsersTable.hisEmpAdmin))
            reference = Firebase.RealTimeRef(Str.PublicMessages).child(Str.ForAdmin);
        else if (PublicChatService.userType.equals(UsersTable.hisPublic))
            reference = Firebase.RealTimeRef(Str.PublicMessages).child(Str.ForUsers).child(userId);

        assert reference != null;
        reference.addChildEventListener(new ChildEventListener() {
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
        PublicMessages message = dataSnapshot.getValue(PublicMessages.class);
        sendMessageBroadcast(message);
        Firebase.addReceivePublicMessageToDB(PublicChatService.this, message);
        dataSnapshot.getRef().removeValue();
    }

    private void sendMessageBroadcast(PublicMessages message) {
        Intent intent = new Intent(getString(R.string.receiveMessageBroadCast));
        intent.putExtra(Static.PublicMsg, message);
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
        unregisterReceiver(receiver);
        serviceThread.quit();
    }
}
