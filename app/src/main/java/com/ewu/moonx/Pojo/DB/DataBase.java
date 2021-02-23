package com.ewu.moonx.Pojo.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public abstract class DataBase extends SQLiteOpenHelper {
    private final static String DataBase_Name = "JournalistMap.db";
    public final String numericType = "NUMERIC";
    protected final String textType = "TEXT";
    protected final String intType = "INTEGER";

    String tableName;
    private Context context;
    private String AddColumens = "";

    public DataBase(Context context, String tableName) {
        super(context, DataBase_Name, null, 1);
        this.context = context;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    protected void CreateTable(SQLiteDatabase db, String TName) {
        db.execSQL("CREATE TABLE " + TName + " ( " + AddColumens + " )");
        AddColumens = "";
    }

    protected void addColumn(DBColumn col, boolean AI) {
        if (!AddColumens.equals(""))
            AddColumens += ",";

        AddColumens += col.getColName() + " " + intType;

        if (AI)
            AddColumens += " PRIMARY KEY  AUTOINCREMENT ";
        else
            AddColumens += " PRIMARY KEY ";
    }

    protected void addColumn(DBColumn col, boolean NN, boolean U) {

        if (!AddColumens.equals(""))
            AddColumens += ",";

        AddColumens += col.getColName() + " " + col.getType();

        if (NN)
            AddColumens += " NOT NULL ";

        if (U)
            AddColumens += " UNIQUE ";
    }

    protected void addColumn(DBColumn col, String value) {
        if (!AddColumens.equals(""))
            AddColumens += ",";

        if (col.getType().equals(textType))
            AddColumens += col.getColName() + " " + col.getType() + " DEFAULT '" + value + "'";
        else
            AddColumens += col.getColName() + " " + col.getType() + " DEFAULT " + value;

    }

    public void execSql(String str) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(str);
    }
    public Cursor select(String selectStr) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectStr, null);
    }

}
