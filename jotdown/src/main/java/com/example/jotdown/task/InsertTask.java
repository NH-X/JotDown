package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

public class InsertTask extends AsyncTask<NodeInfo,Void,Void> {
    private static final String TAG="InsertTask";

    private MainApplication myApp;
    private NodesDBHelper helper;                           //数据库帮助器

    private MutableLiveData<Resource<Boolean>> mRequestSchedule;

    public InsertTask(MutableLiveData<Resource<Boolean>> requestSchedule){
        this.myApp=MainApplication.getInstance();
        this.helper=myApp.getNodesDBHelper();
        this.mRequestSchedule=requestSchedule;
    }

    @Override
    protected Void doInBackground(NodeInfo... nodeInfos) {
        NodeInfo node=nodeInfos[0];

        if (!helper.writeIsOpen()) {
            Log.d(TAG, "doInBackground: writeDB is close");
            helper.getWriteDB();
        }
        long result=helper.add(node);
        if(result != -1){
            node._id= result;
            mRequestSchedule.postValue(new Resource<>(
                    true,
                    QueryProcessType.insert_successful,
                    "成功"
            ));
        } else {
            mRequestSchedule.postValue(new Resource<>(
                    false,
                    QueryProcessType.insert_failing,
                    "失败"
            ));
        }
        return null;
    }
}
