package com.example.jotdown.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public abstract class DBHelper extends SQLiteOpenHelper {
    private static final String TAG="DBHelper";
    protected Context context;

    protected int version;                                //数据库版本号
    protected String tableName;                           //表名
    protected static String dbName="nodes.sqlite";        //数据库名

    protected SQLiteDatabase readDB;
    protected SQLiteDatabase writeDB;

    protected String selectSQL;
    protected String createSQL;

    //数据库帮助器的构造器
    protected DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbName, factory, version);
        this.context=context;
        this.version=version;
        this.readDB=getReadableDatabase();
        this.writeDB=getWritableDatabase();
        Log.d(TAG, "DBHelper: version:"+version);
    }
    // onCreate方法
    @Override
    public abstract void onCreate(SQLiteDatabase db);

    //改变数据表结构
    @Override
    public abstract void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);

    //删除一行数据
    protected long deleteById(long rowId) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[] { String.valueOf(rowId) };
        int deletedRows = writeDB.delete(tableName, whereClause, whereArgs);
        return deletedRows > 0 ? rowId : -1;
    }

    //删除所有数据
    protected void deleteAll(){
        String deleteSQL=String.format("delete from %s;",tableName);
        Log.d(TAG, "deleteAll: deleteSQL="+deleteSQL);
        writeDB.execSQL(deleteSQL);
    }

    //查询符合的数据
    protected abstract List<?> queryInfo(String sql);

    //查询某一列数据
    protected List<?> queryInfoById(int id){
        String sql=String.format("%s where _id=%d;",selectSQL,id);
        return queryInfo(sql);
    }

    //查询所有数据
    protected abstract List<?> queryInfoAll();

    //增加一行数据
    protected abstract long add(Object obj);

    //修改一行数据
    protected abstract long update(Object obj);

    //判断写数据库是否打开
    public boolean writeIsOpen(){
        return writeDB.isOpen();
    }

    //判断读数据库是否打开
    public boolean readIsOpen(){
        return readDB.isOpen();
    }

    //获取写数据库
    public void getWriteDB(){
        writeDB=getWritableDatabase();
    }

    //获取读数据库
    public void getReadDB(){
        readDB=getReadableDatabase();
    }
}