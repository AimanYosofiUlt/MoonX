package com.ewu.moonx.ViewModels;

import android.app.Activity;
import android.database.Cursor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ewu.moonx.App.Static;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DBOrder;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;

import java.text.ParseException;

public class ChatViewModel extends ViewModel {
    Activity con;
    MutableLiveData<PublicMessages> publicMessage;
    Cursor chatCursor;

    String senderId, receiverUId, senderName;
    private boolean isPublicChat;
    int messageCount = 0;

    public ChatViewModel() {
        publicMessage = new MutableLiveData<>();
    }

    public void init(Activity con, String senderId, String receiverUId, String senderName, boolean isPublicChat) {
        this.con = con;
        this.senderId = senderId;
        this.receiverUId = receiverUId;
        this.senderName = senderName;
        this.isPublicChat = isPublicChat;
        this.messageCount = 0;
        initData();
    }

    private void initData() {
        PublicMessagesTable table = new PublicMessagesTable(con);
        chatCursor = DB.selectAll().from(table).orderBy(table.dateCol, DBOrder.DESC).start();
        messageCount = chatCursor.getCount();
        addFromDBToLayout();
    }

    public MutableLiveData<PublicMessages> addFromDBToLayout() {
        if (messageCount > 1) {
            int d = 0;
            while (chatCursor.moveToNext() && d < 20) {
                d++;
                publicMessage.postValue(getPublicMessage());
                messageCount--;
            }
        }
        return publicMessage;
    }

    private PublicMessages getPublicMessage() {
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
        return message;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        chatCursor.close();
    }
}
