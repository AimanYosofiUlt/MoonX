package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import android.database.Cursor;

import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_OrderByHandler {
    DataBase dataBase;
    public DB_OrderByHandler(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public Cursor start()
    {
        return dataBase.select(DB.getStatement());
    }
}