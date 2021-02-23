package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.DataBase;

public class DB_SingleSelectHandler {
    public DB_SingleFromHandler from(DataBase table) {
        DB.addToStatement(" From " + table.getTableName());
        return new DB_SingleFromHandler(table);
    }
}