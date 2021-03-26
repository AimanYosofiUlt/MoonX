package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_SingleFromHandler extends GroubByCode {
    protected DataBase dataBase;

    public DB_SingleFromHandler(DataBase dataBase) {
        super(dataBase);
        this.dataBase = dataBase;
    }
}
