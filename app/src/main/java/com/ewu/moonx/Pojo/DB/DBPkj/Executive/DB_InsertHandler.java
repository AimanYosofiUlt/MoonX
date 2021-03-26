package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_InsertHandler {

    public DB_InsertHandler insert(DBColumn col, String val) {
        DB.addInsertStr(col.getColName(), "'" + val + "'");
        return new DB_InsertHandler();
    }

    public DB_InsertHandler insert(DBColumn col, double val) {
        DB.addInsertStr(col.getColName(), "" + val);
        return new DB_InsertHandler();
    }

    public void inTo(DataBase dataBase) {
        dataBase.execSql(" Insert Into " + dataBase.getTableName()
                + " ( " + DB.getInsertColsStr() + " ) Values ( " + DB.getInsertValuesStr() + ")");
    }
}
