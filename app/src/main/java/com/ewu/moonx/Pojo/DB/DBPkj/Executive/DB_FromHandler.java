package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_FromHandler extends DB_SingleFromHandler {

    public DB_FromHandler(DataBase dataBase) {
        super(dataBase);
    }

    public DB_FromHandler from(DataBase table) {
        DB.addToStatement(" , " + table.getTableName());
        return new DB_FromHandler(table);
    }
}
