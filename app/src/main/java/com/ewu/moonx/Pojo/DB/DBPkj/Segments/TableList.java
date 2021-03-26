package com.ewu.moonx.Pojo.DB.DBPkj.Segments;

import com.ewu.moonx.Pojo.DB.Tables.DataBase;

import java.util.ArrayList;

public  class TableList {
    ArrayList<DataBase> tables;
    public TableList()
    {
        tables = new ArrayList<>();
    }

    public void addTable(DataBase table)
    {
        tables.add(table);
    }

    public DataBase getTable(int index)
    {
        return tables.get(index);
    }

    public int getTablesCount()
    {
        return  tables.size();
    }
}