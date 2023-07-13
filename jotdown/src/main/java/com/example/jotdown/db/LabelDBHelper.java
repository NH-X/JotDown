package com.example.jotdown.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.jotdown.R;
import com.example.jotdown.bean.LabelInfo;
import com.example.jotdown.bean.NodeInfo;

import java.util.ArrayList;
import java.util.List;

public class LabelDBHelper extends DBHelper {
    private static final String TAG = "LabelDBHelper";

    protected LabelDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        tableName = "Labels";
        selectSQL = String.format("select _id,importance from %s ", tableName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSQL=String.format(
                "create table if not exists %s(" +
                        "_id integer primary key autoincrement not null," +
                        "importance varchar not null);",
                tableName
        );
        Log.d(TAG, "onCreate: createSQL is "+createSQL);
        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        sqLiteDatabase.execSQL(String.format("DROP TABLE IF EXISTS %s",tableName));

        // Call onCreate to create the new table
        onCreate(sqLiteDatabase);
    }

    @Override
    protected List<LabelInfo> queryInfo(String sql) {
        Log.d(TAG, "queryInfo: sql is "+sql);
        List<LabelInfo> labelArray=new ArrayList<>();
        Cursor cursor=readDB.rawQuery(sql,null);

        while(cursor.moveToNext()){
            LabelInfo info=new LabelInfo(context);
            info._id=cursor.getInt(0);
            info.importance=cursor.getString(1);

            labelArray.add(info);
        }
        cursor.close();

        return labelArray;
    }

    @Override
    public List<LabelInfo> queryInfoById(long id){
        return (List<LabelInfo>) super.queryInfoById(id);
    }

    @Override
    protected List<LabelInfo> queryInfoAll() {
        return queryInfo(selectSQL+";");
    }

    @Override
    protected long add(Object obj) {
        LabelInfo info= (LabelInfo) obj;
        long result;

        if(info._id>-1){
            result = info._id;
            update(info);
        }
        else {
            ContentValues cv=new ContentValues();
            cv.put("_id",info._id);
            cv.put("importance",info.importance);
            result=writeDB.insert(tableName,"",cv);
            Log.d(TAG, "add: result is "+result);
        }
        return result;
    }

    @Override
    protected long update(Object obj) {
        LabelInfo info= (LabelInfo) obj;
        ContentValues cv=new ContentValues();
        cv.put("_id",info._id);
        cv.put("importance",info.importance);

        if(info._id>-1){
            String whereClause="_id=?";
            String[] whereArgs={String.valueOf(info._id)};

            return writeDB.update(tableName,cv,whereClause,whereArgs);
        }
        return -1;
    }

    @Override
    public long deleteById(long rowId){
        return super.deleteById(rowId);
    }

    @Override
    public void deleteAll(){
        super.deleteAll();
    }

    // 查询数据库中数据行数
    public int queryCount(){
        Cursor cursor= readDB.rawQuery(selectSQL,null);
        int count=cursor.getCount();
        cursor.close();
        Log.d(TAG, "queryCount: count is "+count);

        return count;
    }
}