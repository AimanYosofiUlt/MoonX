package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import android.database.Cursor;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DB_GroubByHandler extends OrderCode {
    private final DataBase dataBase;

    public DB_GroubByHandler(DataBase dataBase) {
        super(dataBase);
        this.dataBase = dataBase;
    }

    public DB_OrderByHandler orderBy(DBColumn column) {
        DB.addToStatement(" ORDER BY " + column.getName());
        return new DB_OrderByHandler(dataBase);
    }


}
