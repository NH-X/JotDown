package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

// 该类已弃用，更好的替代方式是使用RxJava and RxAndroid
@Deprecated
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
        try{
            helper.update(node);
            mRequestSchedule.postValue(new Resource<>(
                    true,
                    ProcessType.update_successful,
                    "成功"
            ));
        } catch (Exception e) {
            mRequestSchedule.postValue(new Resource<>(
                    false,
                    ProcessType.update_failing,
                    e.getMessage()
            ));
        }
        return null;
    }
}