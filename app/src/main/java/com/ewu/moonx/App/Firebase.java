package com.ewu.moonx.App;

import android.content.Context;

import com.ewu.moonx.Pojo.DB.DBPkj.Executive.DB;
import com.ewu.moonx.Pojo.DB.Models.PublicMessages;
import com.ewu.moonx.Pojo.DB.Models.UserMessages;
import com.ewu.moonx.Pojo.DB.Tables.MessageTable;
import com.ewu.moonx.Pojo.DB.Tables.PublicMessagesTable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {

    public static DatabaseReference RealTimeRef(String parent) {
        return FirebaseDatabase.getInstance().getReference(parent);
    }

    public static CollectionReference FireCloudRef(String parent) {
        return FirebaseFirestore.getInstance().collection(parent);
    }


    public static StorageReference StorageRef(String parent) {
        return FirebaseStorage.getInstance().getReference(parent);
    }

    public static void addSenderPublicMessageToDB(Context context, PublicMessages publicMessage) {
        addPublicMessageToDB(context, publicMessage, MessageTable.ItsInProgress);
    }

    public static void addReceivePublicMessageToDB(Context context, PublicMessages publicMessage) {
        addPublicMessageToDB(context, publicMessage, MessageTable.StUser_notRead);
    }

    private static void addPublicMessageToDB(Context context, PublicMessages publicMessage, String statue) {
        PublicMessagesTable table = new PublicMessagesTable(context);
        DB.insert(table.idCol, publicMessage.getId())
                .insert(table.userIdCol, publicMessage.getUserId())
                .insert(table.userNameCol, publicMessage.getUserName())
                .insert(table.textCol, publicMessage.getText())
                .insert(table.dateCol, Static.getDateTimeString(publicMessage.getDate()))
                .insert(table.statueCol, statue)
                .insert(table.replayMsgIdCol, publicMessage.getReplayMsgId()).inTo(table);
    }

    public static void addSenderUserMessageToDB(Context con, UserMessages message) {
        if (message.getType().equals(UserMessages.TEXT_TYPE))
            addUserMessageToDB(con, message, MessageTable.ItsInProgress);
        else
            addUserMessageToDB(con, message, MessageTable.ItsWaitContent);
    }

    public static void addReceiveUserMessageToDB(Context con, UserMessages message) {
        if (message.getType().equals(UserMessages.TEXT_TYPE))
            addUserMessageToDB(con, message, MessageTable.StUser_notRead);
        else
            addUserMessageToDB(con, message, MessageTable.StUser_notRead + MessageTable.StUser_needDownload);
    }

    public static void addUserMessageToDB(Context con, UserMessages message, String statue) {
        MessageTable table = new MessageTable(con);
        DB.insert(table.idCol, message.getId())
                .insert(table.senderUidCol, message.getSenderUid())
                .insert(table.receiverUidCol, message.getReceiverUid())
                .insert(table.contentCol, message.getText())
                .insert(table.typeCol, message.getText())
                .insert(table.dateCol, Static.getDateTimeString(message.getDate()))
                .insert(table.statueCol, statue)
                .insert(table.replayMsgIdCol, message.getReplayMsgId()).inTo(table);
    }
}
