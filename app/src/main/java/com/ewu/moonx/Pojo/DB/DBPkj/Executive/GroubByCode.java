package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class GroubByCode extends WhareCode {
    protected DataBase dataBase;

    public GroubByCode(DataBase dataBase) {
        super(dataBase);
        this.dataBase = dataBase;
    }

    public DB_GroubByHandler groubBy(DBColumn column) {
        DB.addToStatement(" GROUP by " + column.getName());
        return new DB_GroubByHandler(dataBase);
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

}
