package com.example.jotdown.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.jotdown.bean.LabelInfo;

import java.util.ArrayList;
import java.util.List;

public class LabelDBHelper extends DBHelper{
    private static final String TAG="LabelDBHelper";

    public LabelDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        tableName="Labels";
        selectSQL=String.format("select _id,labelColor,importance from %s ",tableName);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createSQL="create table if not exists Nodes(" +
                "_id integer primary key autoincrement not null," +
                "labelColor integer not null,importance varchar not null);";
        Log.d(TAG, "onCreate: createSQL="+createSQL);
        sqLiteDatabase.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    protected List<LabelInfo> queryInfo(String sql) {
        Log.d(TAG, "queryInfo: sql="+sql);
        List<LabelInfo> labelArray=new ArrayList<>();
        Cursor cursor=readDB.rawQuery(sql,null);

        while(cursor.moveToNext()){
            LabelInfo info=new LabelInfo(context);
            info._id=cursor.getInt(0);
            info.labelColor=cursor.getInt(1);
            info.importance=cursor.getString(2);
            labelArray.add(info);
        }
        cursor.close();
        return labelArray;
    }

    public List<?> queryInfoByImportance(String importance){
        return queryInfo(String.format("%s where importance = %s;",selectSQL,importance));
    }

    @Override
    public List<?> queryInfoAll() {
        return queryInfo(selectSQL+";");
    }

    @Override
    public long add(Object obj) {
        LabelInfo info=(LabelInfo) obj;
        long result;

        if(info._id>-1){
            result=info._id;
            update(obj);
        }
        else{
            ContentValues cv=new ContentValues();
            cv.put("labelColor",info.labelColor);
            cv.put("importance",info.importance);
            result=writeDB.insert(tableName,"",cv);
            Log.d(TAG, "add: result="+result);
        }
        return result;
    }

    @Override
    public void update(Object obj) {
        LabelInfo info= (LabelInfo) obj;
        @SuppressLint("DefaultLocale")
        String updateSQL=String.format("update %s set labelColor=%d,importance='%s' where ",
                tableName,info.labelColor,info.importance);

        if(info._id>-1){
            updateSQL=String.format("%s _id=%d;",updateSQL,info._id);
        }
        Log.d(TAG, "update: updateSQL="+updateSQL);
        writeDB.execSQL(updateSQL);
    }

    //查询数据库中数据行数
    public int queryCount(){
        Cursor cursor=readDB.rawQuery(selectSQL,null);
        int count=cursor.getCount();
        cursor.close();
        Log.d(TAG, "queryCount: Count="+count);

        return count;
    }
}
