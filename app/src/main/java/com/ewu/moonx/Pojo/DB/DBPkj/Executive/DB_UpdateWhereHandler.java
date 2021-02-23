package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import com.ewu.moonx.Pojo.DB.DataBase;

public class DB_UpdateWhereHandler {
    DataBase dataBase;
    public DB_FromHandler OR;
    public DB_FromHandler AND;

    public static short ASC = 0, DESC = 1;

    public DB_UpdateWhereHandler(DataBase dataBase) {
        this.dataBase = dataBase;
        OR = new DB_FromHandler(dataBase);
        AND = new DB_FromHandler(dataBase);

        OR.setWhereType(DB_FromHandler.ORTYPE);
        AND.setWhereType(DB_FromHandler.ANDTYPE);
    }

    public void start() {
        dataBase.execSql("Update " + dataBase.getTableName() + " Set "  );
    }
}
