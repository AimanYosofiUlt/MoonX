package com.ewu.moonx.Pojo.DB.Tables;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class SettingTable extends DataBase {
    public static final String TableName = "SettingTable";

    public final DBColumn idCol = new DBColumn(this).getStringInstance("Id");
    public final DBColumn firstNameCol = new DBColumn(this).getStringInstance("FirstName");
    public final DBColumn secondNameCol = new DBColumn(this).getStringInstance("SecondName");
    public final DBColumn thirdNameCol = new DBColumn(this).getStringInstance("ThirdName");
    public final DBColumn phoneCol = new DBColumn(this).getStringInstance("Phone");
    public final DBColumn typeCol = new DBColumn(this).getStringInstance("Type");
    public final DBColumn allowUserCol = new DBColumn(this).getStringInstance("AllowUser");
    public final DBColumn signCountCol =new DBColumn(this).getNumericInstance("SignCount");

    //Attr
    public static final String hisJournalist = "JournalistType";
    public static final String hisAdmin = "AdminType";
    public static final String hisBlock = "BlockType";
    public static final String hisEmpAdmin = "BlockType";
    public static final String hisPublic = "Public";


    public final String hisAllowed_WithoutImg = "hisAllowed";
    public final String hisAllowed_WithImg = "hisAllowed_withImg";
    public final String hisNotAllowed = "hisNotAllowed";

    public SettingTable(Context context) {
        super(context, TableName);
    }
}
