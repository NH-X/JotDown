package com.example.jotdown.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.jotdown.bean.NodeInfo;

import java.util.ArrayList;
import java.util.List;

public class NodesDBHelper extends DBHelper{
    private static final String TAG="NodesDBHelper";

    public NodesDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        tableName="Nodes";
        Log.d(TAG, "NodesDBHelper: tableName is "+tableName);
        this.selectSQL=String.format("select _id,title,titleSize," +
                "content,contentSize," +
                "importance,labelColor," +
                "remind,requestCode,audioFilePath from %s ",tableName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        tableName="Nodes";
        Log.d(TAG, "onCreate: tableName is "+tableName);
        createSQL=String.format(
                "create table if not exists %s(" +
                        "_id integer primary key autoincrement not null," +
                        "title varchar,titleSize integer," +
                        "content varchar, contentSize integer," +
                        "importance varchar not null,labelColor integer," +
                        "remind varchar not null,requestCode integer not null," +
                        "audioFilePath varchar not null);"
                ,tableName
        );
        Log.d(TAG, "onCreate: createSQL="+createSQL);
        db.execSQL(createSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Nodes");

        // Call onCreate to create the new table
        onCreate(sqLiteDatabase);
    }

    @Override
    protected List<NodeInfo> queryInfo(String sql) {
        Log.d(TAG, "queryInfo: sql="+sql);
        List<NodeInfo> nodeArray=new ArrayList<>();
        Cursor cursor=readDB.rawQuery(sql,null);

        while(cursor.moveToNext()){
            NodeInfo info=new NodeInfo(context);
            info._id=cursor.getInt(0);
            info.title=cursor.getString(1);
            info.titleSize=cursor.getInt(2);
            info.content=cursor.getString(3);
            info.contentSize=cursor.getInt(4);
            info.importance=cursor.getString(5);
            info.labelColor=cursor.getInt(6);
            info.remind=cursor.getString(7);
            info.requestCode=cursor.getInt(8);
            info.audioFilePath=cursor.getString(9);

            nodeArray.add(info);
        }
        cursor.close();

        return nodeArray;
    }

    public List<NodeInfo> queryInfoBy(String sql){
        return queryInfo(sql);
    }

    @Override
    public List<NodeInfo> queryInfoAll() {
        return queryInfo(selectSQL+";");
    }

    @Override
    public long add(Object node) {
        NodeInfo info=(NodeInfo) node;
        long result;

        if(info._id>-1){
            result= info._id;
            update(info);
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put("title", info.title);
            cv.put("titleSize", info.titleSize);
            cv.put("content", info.content);
            cv.put("contentSize", info.contentSize);
            cv.put("importance", info.importance);
            cv.put("labelColor", info.labelColor);
            cv.put("remind", info.remind);
            cv.put("requestCode",info.requestCode);
            cv.put("audioFilePath",info.audioFilePath);
            result=writeDB.insert(tableName,"",cv);
            Log.d(TAG, "add: result="+result);
        }
        return result;
    }

    @Override
    public long update(Object obj) {
        NodeInfo info=(NodeInfo) obj;
        ContentValues values=new ContentValues();
        values.put("title",info.title);
        values.put("titleSize",info.titleSize);
        values.put("content",info.content);
        values.put("contentSize",info.contentSize);
        values.put("importance",info.importance);
        values.put("labelColor",info.labelColor);
        values.put("remind",info.remind);
        values.put("requestCode",info.requestCode);
        values.put("audioFilePath",info.audioFilePath);

        if (info._id > -1) {
            String whereClause = "_id = ?";
            String[] whereArgs = { String.valueOf(info._id) };

            return writeDB.update(tableName, values, whereClause, whereArgs);
        }
        return -1;
    }

    @Override
    public long deleteById(long rowId) throws Exception {
        return super.deleteById(rowId);
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
    }

    @Override
    public List<NodeInfo> queryInfoById(long id) {
        return (List<NodeInfo>) super.queryInfoById(id);
    }

    //模糊查询某项数据
    @Override
    public List<NodeInfo> queryLikeInfo(String linkStr){
        String sql=String.format("%s where title like '%%%s%%';",selectSQL, linkStr);
        Log.d(TAG, "queryLikeInfo: select sql is "+sql);

        return queryInfo(sql);
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