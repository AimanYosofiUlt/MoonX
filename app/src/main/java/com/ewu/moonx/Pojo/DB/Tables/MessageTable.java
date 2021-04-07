package com.ewu.moonx.Pojo.DB.Tables;

import android.content.Context;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class MessageTable extends DataBase {
    final static String TableName = "MessageTable";

    public DBColumn idCol = new DBColumn(this).getStringInstance("ID");
    public DBColumn senderUidCol = new DBColumn(this).getStringInstance("SenderUid");
    public DBColumn receiverUidCol = new DBColumn(this).getStringInstance("ReceiverUid");
    public DBColumn contentCol = new DBColumn(this).getStringInstance("Content");
    public DBColumn typeCol = new DBColumn(this).getStringInstance("Type");
    public DBColumn dateCol = new DBColumn(this).getStringInstance("Date");
    public DBColumn statueCol = new DBColumn(this).getStringInstance("Statue");
    public DBColumn replayMsgIdCol = new DBColumn(this).getStringInstance("ReplayMsg");

    // Sended Messages
    public static final String ItsInProgress = "ItsInProgress";
    public static final String ItsWaitContent = "ItsWaitContent";
    public static final String ItsSent = "ItsSent";
    public static final String ItsReceived = "ItsReceived";
    public static final String ItsReaded = "ItsReaded";

    // Recived Messages
    public static final String StUser_Read = "StUser_Read";
    public static final String StUser_notRead = "StUser_notRead";
    public static final String StUser_needDownload = "StDownload";

    public MessageTable(Context context) {
        super(context, TableName);
    }
}