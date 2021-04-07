package com.ewu.moonx.Pojo.DB.Tables;

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
    private String AddColumens = "";

    Context context;

    public DataBase(Context context, String tableName) {
        super(context, DataBase_Name, null, 1);
        this.context = context;
        this.tableName = tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        SettingTable settingTable = new SettingTable(context);
        PublicMessagesTable publicMessagesTable = new PublicMessagesTable(context);
        MessageTable messageTable = new MessageTable(context);
        UsersTable usersTable = new UsersTable(context);

        addColumn(settingTable.idCol, true, true);
        addColumn(settingTable.firstNameCol, true, true);
        addColumn(settingTable.secondNameCol, true, true);
        addColumn(settingTable.thirdNameCol, true, true);
        addColumn(settingTable.phoneCol, true, true);
        addColumn(settingTable.typeCol, SettingTable.hisAdmin);
        addColumn(settingTable.allowUserCol, settingTable.hisNotAllowed);
        addColumn(settingTable.signCountCol, true, true);
        createTable(db, SettingTable.TableName);


        addColumn(publicMessagesTable.idCol, true, true);
        addColumn(publicMessagesTable.userIdCol, true, false);
        addColumn(publicMessagesTable.userNameCol, true, false);
        addColumn(publicMessagesTable.textCol, true, false);
        addColumn(publicMessagesTable.dateCol, true, false);
        addColumn(publicMessagesTable.statueCol, true, false);
        addColumn(publicMessagesTable.replayMsgIdCol, false, false);
        createTable(db, PublicMessagesTable.TableName);


        addColumn(messageTable.idCol, true, true);
        addColumn(messageTable.senderUidCol, true, false);
        addColumn(messageTable.receiverUidCol, true, false);
        addColumn(messageTable.contentCol, true, false);
        addColumn(messageTable.typeCol, true, false);
        addColumn(messageTable.dateCol, true, false);
        addColumn(messageTable.statueCol, true, false);
        addColumn(messageTable.replayMsgIdCol, false, false);
        createTable(db, MessageTable.TableName);

        addColumn(usersTable.idCol, true, true);
        addColumn(usersTable.firstNameCol, true, false);
        addColumn(usersTable.secondNameCol, true, false);
        addColumn(usersTable.thirdNameCol, true, false);
        addColumn(usersTable.phoneCol, true, true);
        addColumn(usersTable.typeCol, true, false);
        addColumn(usersTable.statueCol, UsersTable.IsNormal);
        addColumn(usersTable.imageName, true, false);
        createTable(db, UsersTable.TableName);
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    protected void createTable(SQLiteDatabase db, String TName) {
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
