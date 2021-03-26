package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_UpdateSetHandler extends WhareCode {

    DataBase dataBase;

    public DB_UpdateSetHandler(DataBase database) {
        super(database);
        this.dataBase = database;
    }
}
