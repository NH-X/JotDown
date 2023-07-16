package com.example.jotdown.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.jotdown.bean.LabelBean;

import java.util.ArrayList;
import java.util.List;

public class LabelDBHelper extends DBHelper {
    private static final String TAG = "LabelDBHelper";

    public LabelDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.tableName="Labels";
        Log.d(TAG, "LabelDBHelper: tableName is "+tableName);
        selectSQL = String.format("select _id,importance from %s ", tableName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.tableName="Labels";
        Log.d(TAG, "onCreate: tableName is "+tableName);
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
    protected List<LabelBean> queryInfo(String sql) {
        Log.d(TAG, "queryInfo: sql is "+sql);
        List<LabelBean> labelArray=new ArrayList<>();
        Cursor cursor=readDB.rawQuery(sql,null);

        while(cursor.moveToNext()){
            LabelBean info=new LabelBean();
            info._id=cursor.getInt(0);
            info.importance=cursor.getString(1);

            labelArray.add(info);
        }
        cursor.close();

        return labelArray;
    }

    @Override
    public List<LabelBean> queryInfoById(long id){
        return (List<LabelBean>) super.queryInfoById(id);
    }

    //模糊查询某项数据
    @Override
    public List<LabelBean> queryLikeInfo(String linkStr) {
        String sql=String.format("%s where importance like '%%%s%%';",selectSQL,linkStr);
        Log.d(TAG, "queryLikeInfo: select sql is "+sql);

        return queryInfo(sql);
    }

    @Override
    public List<LabelBean> queryInfoAll() {
        return queryInfo(selectSQL+";");
    }

    @Override
    protected long add(Object obj) {
        LabelBean info= (LabelBean) obj;
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
        LabelBean info= (LabelBean) obj;
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