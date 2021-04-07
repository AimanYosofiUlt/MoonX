package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_WhereHandler extends GroubByCode {
    DataBase dataBase;
    public DB_FromHandler or;
    public DB_FromHandler and;

    public DB_WhereHandler(DataBase dataBase) {
        super(dataBase);
        this.dataBase = dataBase;
        or = new DB_FromHandler(dataBase);
        and = new DB_FromHandler(dataBase);

        or.setWhereType(DB_FromHandler.ORTYPE);
        and.setWhereType(DB_FromHandler.ANDTYPE);
    }

    public DB_GroubByHandler groubBy(DBColumn column) {
        DB.addToStatement(" GROUP by " + column.getName());
        return new DB_GroubByHandler(dataBase);
    }

    public DB_OrderByHandler orderBy(DBColumn column) {
        DB.addToStatement(" ORDER BY " + column.getName());
        return new DB_OrderByHandler(dataBase);
    }

    public DB_OrderByHandler orderBy(DBColumn column, short orderType) {
        String orderTypeStr;
        if (orderType == DBOrder.ASC)
            orderTypeStr = "ASC";
        else
            orderTypeStr = "DESC";

        DB.addToStatement(" ORDER BY " + column.getName() + " " + orderTypeStr);

        return new DB_OrderByHandler(dataBase);
    }

}
