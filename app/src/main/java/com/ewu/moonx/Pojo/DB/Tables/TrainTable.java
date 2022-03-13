package com.ewu.moonx.Pojo.DB.Tables;

import android.content.Context;

import com.ewu.moonx.Pojo.DB.DBPkj.Segments.DBColumn;

public class TrainTable extends DataBase {
    final static String TableName = "TrainTable";

    public DBColumn image = new DBColumn(this).getStringInstance("ID");

    public TrainTable(Context context) {
        super(context, TableName);
    }
}