package com.ewu.moonx.Pojo.DB.Tables;

import android.content.Context;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class MessageTable extends DataBase {
    final static String TableName = "MessageTable";

    DBColumn idCol = new DBColumn(this).getStringInstance("ID");
    DBColumn senderUidCol = new DBColumn(this).getStringInstance("SenderUid");
    DBColumn receiverUidCol = new DBColumn(this).getStringInstance("ReceiverUid");
    DBColumn contentCol = new DBColumn(this).getStringInstance("Content");
    DBColumn typeCol = new DBColumn(this).getStringInstance("Type");
    DBColumn dateCol = new DBColumn(this).getStringInstance("Date");
    DBColumn statueCol = new DBColumn(this).getStringInstance("Statue");
    DBColumn replayMsgIdCol = new DBColumn(this).getStringInstance("ReplayMsg");

    // Sended Messages
    public static final String ItsInProgress = "ItsInProgress";
    public static final String ItsWaitContent = "ItsWaitContent";
    public static final String ItsSent = "ItsSent";
    public static final String ItsReceived = "ItsReceived";
    public static final String ItsReaded = "ItsReaded";

    // Recived Messages
    public static final String StUser_Read = "StUser_Read";
    public static final String StUser_notRead = "StUser_notRead";

    public MessageTable(Context context) {
        super(context, TableName);
    }
}