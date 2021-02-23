package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class DB_SelectHandler extends DB_SingleSelectHandler {
    public DB_SelectHandler select(DBColumn column) {
        DB.addToStatement(" , " + column.getName());
        return new DB_SelectHandler();
    }
}
