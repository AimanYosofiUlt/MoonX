package com.ewu.moonx.Pojo.DB.DBPkj.Executive;
import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class DB {
    private static String dbStr;
    private static String insertColsStr, insertValuesStr;
    private static String updateStr;

    private static String sqlStmtType;
    public static final String UPDATE_STMT = "UPDATE";
    public static final String SELECT_STMT = "SELECT";
    public static final String INSERT_STMT = "INSERT";

    static void addToStatement(String str) {
        dbStr += str;
    }

    static String getStatement() {
        return dbStr;
    }

    public static void addInsertStr(String column, String val) {
        insertColsStr += " ," + column;
        insertValuesStr += " ," + val;
    }

    public static String getUpdateStr() {
        return updateStr;
    }

    public static void addUpdateStr(String str) {
        updateStr += " ," + str;
    }

    static String getInsertColsStr() {
        return insertColsStr;
    }

    static String getInsertValuesStr() {
        return insertValuesStr;
    }

    public static DB_SelectHandler select(DBColumn column) {
        dbStr = "Select " + column.getName();
        sqlStmtType = SELECT_STMT;
        return new DB_SelectHandler();
    }

    public static DB_SingleSelectHandler selectAll() {
        dbStr = " Select * ";
        sqlStmtType = SELECT_STMT;
        return new DB_SelectHandler();
    }

    public static DB_SingleSelectHandler selectMax(DBColumn column) {
        dbStr = "Select Max(" + column + ") ";
        sqlStmtType = SELECT_STMT;
        return new DB_SingleSelectHandler();
    }

    public static DB_SingleSelectHandler selectCount(DBColumn column) {
        dbStr = "Select Count(" + column + ") ";
        sqlStmtType = SELECT_STMT;
        return new DB_SingleSelectHandler();
    }

    public static DB_InsertHandler insert(DBColumn col, String val) {
        insertColsStr = col.getColName();
        insertValuesStr = "'" + val + "'";
        sqlStmtType = INSERT_STMT;
        return new DB_InsertHandler();
    }

    public static DB_InsertHandler insert(DBColumn col, double val) {
        insertColsStr = col.getColName();
        insertValuesStr = "" + val;
        sqlStmtType = INSERT_STMT;
        return new DB_InsertHandler();
    }

    public static DB_UpdateHandler set(DBColumn col, String val) {
        updateStr = col.getColName() + " = '" + val + "'";
        sqlStmtType = UPDATE_STMT;
        dbStr ="";
        return new DB_UpdateHandler();
    }

    public static DB_UpdateHandler set(DBColumn col, double val) {
        updateStr = col.getColName() + " = " + val;
        sqlStmtType = UPDATE_STMT;
        dbStr ="";
        return new DB_UpdateHandler();
    }
}