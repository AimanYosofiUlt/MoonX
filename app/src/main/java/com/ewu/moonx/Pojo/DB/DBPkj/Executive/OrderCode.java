package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import android.database.Cursor;
import android.util.Log;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class OrderCode {
    DataBase dataBase;

    public OrderCode(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public DB_OrderByHandler orderBy(DBColumn column) {
        return startOrder(column, DBOrder.ASC,false);
    }

    public DB_OrderByHandler orderBy(DBColumn column, short orderType) {
        return startOrder(column,orderType,false);
    }

    public DB_OrderByHandler orderByMax(DBColumn column) {
        return startOrder(column, DBOrder.ASC, true);
    }

    public DB_OrderByHandler orderByMax(DBColumn column, short orderType) {
        return startOrder(column, orderType, true);
    }

    public DB_OrderByHandler startOrder(DBColumn column, short orderType, boolean isMax) {
        String orderTypeStr, columnName;

        if (orderType == DBOrder.ASC)
            orderTypeStr = " ASC";
        else
            orderTypeStr = " DESC";

        if (isMax)
            columnName = "Max (" + column.getName() + ")";
        else
            columnName = column.getName();


        DB.addToStatement(" ORDER BY " + columnName + orderTypeStr);

        return new DB_OrderByHandler(dataBase);
    }

    public final Cursor start() {
        if (DB.getSqlStmtType().equals(DB.SELECT_STMT))
            return dataBase.select(DB.getStatement());
        throw getCondationExption("you can't use start with " + DB.getSqlStmtType() + " Statment");
    }

    public final void exec() {
        dataBase.execSql(DB.getStatement());
    }
    private RuntimeException getCondationExption(String message) {
        RuntimeException exception = new RuntimeException();
        Log.e("DBSql Error: ", message, exception);
        return exception;
    }
}
