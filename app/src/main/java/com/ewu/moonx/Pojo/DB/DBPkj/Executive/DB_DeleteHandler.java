package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_DeleteHandler extends WhareCode {
    DataBase dataBase;

    public DB_DeleteHandler(DataBase dataBase) {
        super(dataBase);
        this.dataBase = dataBase;
    }
}
