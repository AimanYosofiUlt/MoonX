package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import android.database.Cursor;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.DataBase;

public class DB_WhereHandler {
    DataBase dataBase;
    public DB_FromHandler OR;
    public DB_FromHandler AND;

    public static short ASC = 0, DESC = 1;

    public DB_WhereHandler(DataBase dataBase) {
        this.dataBase = dataBase;
        OR = new DB_FromHandler(dataBase);
        AND = new DB_FromHandler(dataBase);

        OR.setWhereType(DB_FromHandler.ORTYPE);
        AND.setWhereType(DB_FromHandler.ANDTYPE);
    }

    public DB_OrderByHandler orderBy(DBColumn column) {
        DB.addToStatement(" ORDER BY " + column.getName());
        return new DB_OrderByHandler(dataBase);
    }

    public DB_OrderByHandler orderBy(DBColumn column, short orderType) {
        String orderTypeStr;
        if (orderType == ASC)
            orderTypeStr = "ASC";
        else
            orderTypeStr = "DESC";

        DB.addToStatement(" ORDER BY " + column.getName() + " " + orderTypeStr);

        return new DB_OrderByHandler(dataBase);
    }

    public Cursor start() {
        return dataBase.select(DB.getStatement());
    }
}
