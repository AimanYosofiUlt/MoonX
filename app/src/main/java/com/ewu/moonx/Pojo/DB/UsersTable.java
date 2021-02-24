package com.ewu.moonx.Pojo.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class UsersTable extends DataBase {
    public static final String name = "Users";

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

    public final String hisAllowed = "hisAllowed";
    public final String hisNotAllowed = "hisNotAllowed";

    public UsersTable(Context context) {
        super(context, name);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        addColumn(idCol, true,true);
        addColumn(firstNameCol, true, true);
        addColumn(secondNameCol, true, true);
        addColumn(thirdNameCol, true, true);
        addColumn(phoneCol, true, true);
        addColumn(typeCol, hisAdmin);
        addColumn(allowUserCol,hisNotAllowed);
        addColumn(signCountCol,true,true);
        CreateTable(sqLiteDatabase, name);
    }
}
