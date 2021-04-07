package com.ewu.moonx.Pojo.DB.Tables;

import android.content.Context;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class UsersTable extends DataBase {
    public static final String TableName = "UsersTable";

    public final DBColumn idCol = new DBColumn(this).getStringInstance("Id");
    public final DBColumn firstNameCol = new DBColumn(this).getStringInstance("FirstName");
    public final DBColumn secondNameCol = new DBColumn(this).getStringInstance("SecondName");
    public final DBColumn thirdNameCol = new DBColumn(this).getStringInstance("ThirdName");
    public final DBColumn phoneCol = new DBColumn(this).getStringInstance("Phone");
    public final DBColumn typeCol = new DBColumn(this).getStringInstance("Type");
    public final DBColumn imageName = new DBColumn(this).getStringInstance("ImageNamw");
    public final DBColumn statueCol = new DBColumn(this).getStringInstance("Statue");

    public static final String IsNormal = "IsNormal";
    public static final String IsDeleted = "IsDeleted";
    public static final String IsBlocked = "IsBlocked";

    public UsersTable(Context context) {
        super(context, TableName);
    }
}
