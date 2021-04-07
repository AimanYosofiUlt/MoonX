package com.ewu.moonx.UI.FollowUpPkj.ChatPkj;

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
import com.ewu.moonx.App.Status;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DBOrder;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;
import com.ewu.moonx.Pojo.DB.Models.Users;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.ewu.moonx.Pojo.DB.Tables.UsersTable;
import com.ewu.moonx.R;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatViewPkj.AdminUserChatView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatViewPkj.PublicUserChatView;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.ChatViewPkj.UserChatView;

import java.text.ParseException;
import java.util.ArrayList;

public class ChatFragment extends Fragment {

    Activity context;
    View mainView;
    LinearLayout publicMsgLL, usersMsgLL;
    BroadcastReceiver broadcastReceiver;
    ArrayList<PublicUserChatView> publicUserViews;
    ArrayList<AdminUserChatView> adminUsersViews;

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
        context.registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.receive_PublicMessageBroadCast)));
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
        usersMsgLL = mainView.findViewById(R.id.usersMsgLL);
        publicUserViews = new ArrayList<>();
    }

    private void selectLastMessages() {
        SelectLastPublicMessages();
        SelectLastUserMessages();
    }

    private void SelectLastUserMessages() {
        MessageTable table = new MessageTable(context);

        Cursor chatCursor = DB.selectAll().from(table).
                groubBy(table.senderUidCol).groubBy(table.receiverUidCol).
                orderByMax(table.dateCol, DBOrder.DESC).start();

        chatCursor.close();

        Cursor userCursor = DB.selectAll().from(new UsersTable(context)).start();
        if (userCursor.getCount() > 0) {
            while (userCursor.moveToNext()) {
                Users user = new Users();
                user.setId(userCursor.getString(0));
                user.setFirstName(userCursor.getString(1));
                user.setSecondName(userCursor.getString(2));
                user.setThirdName(userCursor.getString(3));
                user.setPhone(userCursor.getString(4));
                user.setImageName(userCursor.getString(5));
                user.setType(userCursor.getString(6));
                AdminUserChatView chatView = new AdminUserChatView(context, user, new UserMessages());

                if (!user.getId().equals(Static.getUid()))
                    usersMsgLL.addView(chatView.getMainView());

            }
        }
        userCursor.close();
    }

    private void SelectLastPublicMessages() {
        PublicMessagesTable table = new PublicMessagesTable(context);
        Cursor publicChatCursor = DB.selectAll().from(table).groubBy(table.userIdCol).orderByMax(table.dateCol, DBOrder.DESC).start();
        assert publicChatCursor != null;

        while (publicChatCursor.moveToNext()) {
            try {
                PublicMessages publicMessage = new PublicMessages(
                        publicChatCursor.getString(0),
                        publicChatCursor.getString(1),
                        publicChatCursor.getString(2),
                        publicChatCursor.getString(3),
                        Static.getDate(publicChatCursor.getString(4))
                        , publicChatCursor.getString(6));

                PublicUserChatView userChatView = new PublicUserChatView(context, publicMessage, publicChatCursor.getString(5));
                addToLayout(userChatView);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        publicChatCursor.close();
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
