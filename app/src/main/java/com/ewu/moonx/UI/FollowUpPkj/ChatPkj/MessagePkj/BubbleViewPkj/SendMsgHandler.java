package com.ewu.moonx.UI.FollowUpPkj.ChatPkj.MessagePkj.BubbleViewPkj;

import android.content.Context;

import com.ewu.moonx.App.Firebase;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.FireBaseTemplate.Str;
import com.ewu.moonx.Pojo.DB.Tables.SettingTable;

public class SendMsgHandler {
    Context con;
    PublicMessages publicMessage;
    SendListener listener;
    String userType;

    public void setListener(SendListener listener) {
        this.listener = listener;
    }

    interface SendListener {
        void onComplete();
        void onFailure(String eMessage);
    }

    SendMsgHandler(Context con, PublicMessages publicMessage, String userType) {
        this.con = con;
        this.publicMessage = publicMessage;
        this.userType = userType;
    }

    public void send() {
        if (publicMessage != null) {
            sendPublicMessage();
        } else {
            sendMessage();
        }
    }

    private void sendMessage() {

    }

    private void sendPublicMessage() {
        String userStr;
        if (userType.equals(SettingTable.hisEmpAdmin))
            userStr = Str.ForAdmin;
        else
            userStr = Str.ForUsers;

        Firebase.RealTimeRef(Str.PublicMessages)
                .child(userStr)
                .child(publicMessage.getUserId())
                .child(publicMessage.getId())
                .setValue(publicMessage)
                .addOnSuccessListener(aVoid -> listener.onComplete())
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

}
