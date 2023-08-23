package com.example.jotdown.task;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

public class QueryNodeThread extends Thread {
    private final static String TAG = "QueryNodeThread";

    private final NodesDBHelper helper;                           //数据库帮助器

    private final MutableLiveData<Resource<NodeInfo>> mRequestSchedule;
    private long rowId;

    public QueryNodeThread(
            long rowId,
            MutableLiveData<Resource<NodeInfo>> requestSchedule){
        this.helper=MainApplication.
                getInstance().
                getNodesDBHelper();
        this.mRequestSchedule=requestSchedule;
        this.rowId=rowId;
    }

    @Override
    public void run() {
        super.run();
        if(helper == null || !helper.readIsOpen()){
            Log.d(TAG, "run: readDB is close");
            helper.getReadDB();
        }
        NodeInfo info= helper.queryInfoById(rowId).get(0);
        if(info==null){
            Log.d(TAG, "run: 查询失败");
            mRequestSchedule.postValue(new Resource<>(
                    info,
                    ProcessType.query_failing,
                    "查询失败"
            ));
        }
        else{
            Log.d(TAG, "run: 查询成功");
            mRequestSchedule.postValue(new Resource<>(
                    info,
                    ProcessType.query_successful,
                    "查询成功"
            ));
        }
    }
}
