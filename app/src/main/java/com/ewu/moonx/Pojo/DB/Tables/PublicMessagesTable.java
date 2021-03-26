package com.ewu.moonx.Pojo.DB.Tables;

import android.content.Context;
import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
public class PublicMessagesTable extends DataBase {
    public static final String TableName = "PublicMessagesTable";

    public DBColumn idCol = new DBColumn(this).getStringInstance("Id");
    public DBColumn userIdCol = new DBColumn(this).getStringInstance("UserId");
    public DBColumn userNameCol = new DBColumn(this).getStringInstance("UserName");
    public DBColumn textCol = new DBColumn(this).getStringInstance("Text");
    public DBColumn dateCol = new DBColumn(this).getStringInstance("Date");
    public DBColumn statueCol = new DBColumn(this).getStringInstance("Statue");
    public DBColumn replayMsgIdCol = new DBColumn(this).getStringInstance("ReplayMsg");

    public PublicMessagesTable(Context context) {
        super(context, TableName);
    }
}