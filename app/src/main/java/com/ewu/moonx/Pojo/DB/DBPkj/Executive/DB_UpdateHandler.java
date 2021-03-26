package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import android.widget.Toast;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;
import com.ewu.moonx.UI.FollowUpPkj.ChatPkj.PublicChatPkj.PublicChatService;

public class DB_UpdateHandler {
    public DB_UpdateHandler set(DBColumn col, String val) {
        DB.addUpdateStr(col.getColName() + " = '" + val + "'");
        return new DB_UpdateHandler();
    }

    public DB_UpdateHandler set(DBColumn col, double val) {
        DB.addUpdateStr(col.getColName() + " = " + val);
        return new DB_UpdateHandler();
    }

    public DB_UpdateSetHandler update(DataBase table) {
        return new DB_UpdateSetHandler(table);
    }


}
