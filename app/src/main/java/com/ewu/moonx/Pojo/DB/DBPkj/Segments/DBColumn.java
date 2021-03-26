package com.ewu.moonx.Pojo.DB.DBPkj.Segments;

import com.ewu.moonx.Pojo.DB.Tables.DataBase;

public class DBColumn {
    String name, tableName;
    String type;

    public static final String textType = "TEXT";
    public static final String numericType = "NUMERIC";

    public DBColumn(DataBase dataBase) {
        this.tableName = dataBase.getTableName();
    }

    public String getName() {
        return tableName + "." + name;
    }

    public String getColName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public DBColumn getStringInstance(String name) {
        this.name = name;
        type = textType;
        return this;
    }

    public DBColumn getNumericInstance(String name) {
        this.name = name;
        type = numericType;
        return this;
    }
}
