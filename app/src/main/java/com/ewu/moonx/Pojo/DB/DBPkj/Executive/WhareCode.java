package com.ewu.moonx.Pojo.DB.DBPkj.Executive;

import android.database.Cursor;
import android.util.Log;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;
import com.ewu.moonx.Pojo.DB.Tables.DataBase;

import static android.content.ContentValues.TAG;

public class WhareCode {
    protected DataBase dataBase;

    public WhareCode(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public final static short NOTHINGTYPE = 0, ORTYPE = 1, ANDTYPE = 2;
    private short whereType = NOTHINGTYPE;

    void setWhereType(short type) {
        whereType = type;
    }

    public DB_WhereHandler where(DBColumn column, double value) {
        return WhereProcess(column, DBCondation.EQUAL, value);
    }

    public DB_WhereHandler where(DBColumn column, String value) {
        return WhereProcess(column, DBCondation.EQUAL, value);
    }

    public DB_WhereHandler where(DBColumn column, short condation, double value) {
        if (column.getType().equals(DBColumn.numericType)) {
            return WhereProcess(column, condation, value);
        } else {
            throw getCondationExption("condation");
        }
    }

    public DB_WhereHandler where(DBColumn column, short condation, String value) {
        if (column.getType().equals(DBColumn.textType)) {
            return WhereProcess(column, condation, value);
        } else {
            throw getCondationExption("condation");
        }
    }

    private DB_WhereHandler WhereProcess(DBColumn column, short condation, Object value) {
        String whereStatment;

        if (whereType == ANDTYPE)
            whereStatment = " AND ";
        else if (whereType == ORTYPE)
            whereStatment = " OR ";
        else
            whereStatment = " Where ";


        String opr = "";
        switch (condation) {
            case DBCondation.EQUAL:
                opr = "=";
                break;

            case DBCondation.NOTEQUAL:
                opr = "<>";
                break;

            case DBCondation.BIGERTHAN:
                opr = ">";
                break;

            case DBCondation.SMALLERTHAN:
                opr = "<";
                break;

            case DBCondation.BIGER_OR_EQUAL:
                opr = ">=";
                break;

            case DBCondation.SMALLER_OR_EQUAL:
                opr = "<=";
                break;


            default:
                throw getCondationExption("condation");
        }

        if (column.getType().equals(DBColumn.numericType))
            whereStatment += column.getName() + opr + value.toString();
        else
            whereStatment += column.getName() + opr + "'" + value.toString() + "'";

        DB.addToStatement(whereStatment);

        return new DB_WhereHandler(dataBase);
    }

    public DB_WhereHandler Where_LIKE(DBColumn column, short likeCondition, String value) {
        switch (likeCondition) {
            case DBLikeCondation.BEGIN_WITH:
                DB.addToStatement(column.getName() + " LIKE '" + value + "%'");
                break;

            case DBLikeCondation.END_WITH:
                DB.addToStatement(column.getName() + " LIKE '%" + value + "'");
                break;

            case DBLikeCondation.CONTAIN:
                DB.addToStatement(column.getName() + " LIKE '%" + value + "%'");
                break;

            default:
                throw getCondationExption("likeCondition");
        }

        return new DB_WhereHandler(dataBase);
    }

    private RuntimeException getCondationExption(String message) {
        RuntimeException exception = new RuntimeException();
        Log.e("DBSql Error: ", message, exception);
        return exception;
    }

    public final Cursor start() {
        if (DB.getSqlStmtType().equals(DB.SELECT_STMT))
            return dataBase.select(DB.getStatement());
        throw getCondationExption("you can't use start with " + DB.getSqlStmtType() + " Statment");
    }

    public final void exec() {
        if (DB.getSqlStmtType().equals(DB.UPDATE_STMT))
            dataBase.execSql("Update " + dataBase.getTableName() + " Set " + DB.getUpdateStr() + DB.getStatement());
        else
            dataBase.execSql(DB.getStatement());
    }

}
