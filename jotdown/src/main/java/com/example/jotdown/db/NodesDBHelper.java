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
        selectSQL=String.format("select _id,title,titleColor,titleSize," +
                "content,contentColor,contentSize," +
                "importance,labelColor,backgroundColor," +
                "remind,requestCode,audioFilePath from %s ",tableName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createSQL="create table if not exists Nodes(" +
                "_id integer primary key autoincrement not null," +
                "title varchar,titleColor integer,titleSize integer," +
                "content varchar,contentColor integer,contentSize integer," +
                "importance varchar not null,labelColor integer,backgroundColor varchar," +
                "remind varchar not null,requestCode integer not null," +
                "audioFilePath varchar not null);";
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
            info.titleColor=cursor.getInt(2);
            info.titleSize=cursor.getInt(3);
            info.content=cursor.getString(4);
            info.contentColor=cursor.getInt(5);
            info.contentSize=cursor.getInt(6);
            info.importance=cursor.getString(7);
            info.labelColor=cursor.getInt(8);
            info.backgroundColor=cursor.getInt(9);
            info.remind=cursor.getString(10);
            info.requestCode=cursor.getInt(11);
            info.audioFilePath=cursor.getString(12);

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
            update(node);
        }
        else {
            ContentValues cv = new ContentValues();
            cv.put("title", info.title);
            cv.put("titleColor", info.titleColor);
            cv.put("titleSize", info.titleSize);
            cv.put("content", info.content);
            cv.put("contentColor", info.contentColor);
            cv.put("contentSize", info.contentSize);
            cv.put("importance", info.importance);
            cv.put("labelColor", info.labelColor);
            cv.put("backgroundColor", info.backgroundColor);
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
        values.put("titleColor",info.titleColor);
        values.put("titleSize",info.titleSize);
        values.put("content",info.content);
        values.put("contentColor",info.contentColor);
        values.put("contentSize",info.contentSize);
        values.put("importance",info.importance);
        values.put("labelColor",info.labelColor);
        values.put("backgroundColor",info.backgroundColor);
        values.put("remind",info.remind);
        values.put("requestCode",info.requestCode);
        values.put("audioFilePath",info.audioFilePath);
        if (info._id > -1) {
            String whereClause = "_id = ?";
            String[] whereArgs = { String.valueOf(info._id) };

            return writeDB.update(tableName, values, whereClause, whereArgs);
        }
        return -1l;
    }

    @Override
    public long deleteById(long rowId) {
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
    public List<NodeInfo> queryLikeInfo(String linkStr){
        String sql=String.format("%s where title like '%%%s%%';",selectSQL, linkStr);
        Log.d(TAG, "queryLinkInfo: SQL="+sql);

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