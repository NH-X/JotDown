package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

public class UpdateTask extends AsyncTask<NodeInfo,Void,Void> {
    private static final String TAG = "UpdateTask";

    private MainApplication myApp;
    private NodesDBHelper helper;                       //数据库帮助器

    private MutableLiveData<Resource<Boolean>> mRequestSchedule;

    public UpdateTask(MutableLiveData<Resource<Boolean>> requestSchedule) {
        this.myApp=MainApplication.getInstance();
        this.mRequestSchedule = requestSchedule;
        this.helper = myApp.getNodesDBHelper();
    }

    @Override
    protected Void doInBackground(NodeInfo... nodeInfos) {
        NodeInfo node = nodeInfos[0];
        if (!helper.writeIsOpen()) {
            Log.d(TAG, "doInBackground: writeDB is close");
            helper.getWriteDB();
        }
        if (helper.update(node) != -1) {
            mRequestSchedule.postValue(new Resource<>(
                    true,
                    QueryProcessType.update_successful,
                    "成功"
            ));
        } else {
            mRequestSchedule.postValue(new Resource<>(
                    false,
                    QueryProcessType.update_failing,
                    "失败"
            ));
        }
        return null;
    }
}