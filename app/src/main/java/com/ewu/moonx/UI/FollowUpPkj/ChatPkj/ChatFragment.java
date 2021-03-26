package com.ewu.moonx.UI.FollowUpPkj.ChatPkj;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DBOrder;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatViewPkj.PublicUserChatView;

import java.text.ParseException;
import java.util.ArrayList;

public class ChatFragment extends Fragment {

    Activity context;
    View mainView;
    LinearLayout publicMsgLL;
    BroadcastReceiver broadcastReceiver;
    ArrayList<PublicUserChatView> publicUserViews;

    public ChatFragment(Activity context) {
        this.context = context;
    }

    public static String currentPublicChatUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mainView = inflater.inflate(R.layout.fragment_followup_chat, container, false);

        currentPublicChatUser = "";
        init();
        selectLastMessages();
        initBroadcast();
        return mainView;
    }

    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                PublicMessages publicMessage = ((PublicMessages) intent.getSerializableExtra(Static.PublicMsg));
                setUpUsersView(publicMessage);
            }

            private void setUpUsersView(PublicMessages publicMessage) {
                if (!publicUserViews.isEmpty() && publicMessage.getId().equals(publicUserViews.get(0).getUserId())) {
                    PublicUserChatView userChatView = publicUserViews.get(0);
                    userChatView.setMessage(publicMessage, MessageTable.StUser_notRead);
                    setCountVisibility(userChatView);
                } else {
                    boolean isNotRepeated = true;
                    for (PublicUserChatView publicUserView : publicUserViews) {
                        if (publicUserView.getMessage().getUserId().equals(publicMessage.getUserId())) {
                            publicUserView.setMessage(publicMessage, MessageTable.StUser_notRead);

                            publicUserViews.remove(publicUserView);
                            publicUserViews.add(0, publicUserView);
                            publicMsgLL.removeView(publicUserView.getMainView());
                            publicMsgLL.addView(publicUserView.getMainView(), 0);

                            setCountVisibility(publicUserView);
                            isNotRepeated = false;
                            break;
                        }
                    }

                    if (isNotRepeated) {
                        addToLayout(new PublicUserChatView(context, publicMessage, MessageTable.ItsReceived));
                    }
                }
            }


        };
        context.registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.receiveMessageBroadCast)));
    }

    private void addToLayout(PublicUserChatView publicUserView) {
        setCountVisibility(publicUserView);
        publicUserViews.add(0, publicUserView);
        publicMsgLL.addView(publicUserView.getMainView());
    }

    private void setCountVisibility(PublicUserChatView publicUserView) {
        if (!currentPublicChatUser.equals(publicUserView.getUserId())) {
            Toast.makeText(context, "currentPublicChatUser", Toast.LENGTH_SHORT).show();
            publicUserView.setCountVisibility();
        }
    }

    private void init() {
        publicMsgLL = mainView.findViewById(R.id.publicMsgLL);
        publicUserViews = new ArrayList<>();
    }

    private void selectLastMessages() {
        SelectLastPublicMessages();
    }

    @SuppressLint("SetTextI18n")
    private void SelectLastPublicMessages() {
        PublicMessagesTable table = new PublicMessagesTable(context);
        Cursor chatCursor = DB.selectAll().from(table).groubBy(table.userIdCol).orderByMax(table.dateCol, DBOrder.DESC).start();
        assert chatCursor != null;

        while (chatCursor.moveToNext()) {
            try {
                PublicMessages publicMessage = new PublicMessages(
                        chatCursor.getString(0),
                        chatCursor.getString(1),
                        chatCursor.getString(2),
                        chatCursor.getString(3),
                        Static.getDate(chatCursor.getString(4))
                        , chatCursor.getString(6));

                PublicUserChatView userChatView = new PublicUserChatView(context, publicMessage, chatCursor.getString(5));
                addToLayout(userChatView);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        chatCursor.close();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        currentPublicChatUser = "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastReceiver);
    }
}
