package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import android.database.Cursor;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_OrderByHandler {
    DataBase dataBase;

    public DB_OrderByHandler(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public DB_OrderByHandler orderBy(DBColumn column) {
        return startOrder(column, DBOrder.ASC);
    }

    public DB_OrderByHandler orderBy(DBColumn column, short orderType) {
        return startOrder(column, orderType);
    }

    public DB_OrderByHandler startOrder(DBColumn column, short orderType) {
        String orderTypeStr;

        if (orderType == DBOrder.ASC)
            orderTypeStr = " ASC";
        else
            orderTypeStr = " DESC";

        DB.addToStatement(" , " + column.getName() + orderTypeStr);

        return new DB_OrderByHandler(dataBase);
    }

    public Cursor start() {
        return dataBase.select(DB.getStatement());
    }
}