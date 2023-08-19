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
public class InsertTask extends AsyncTask<NodeInfo,Void,Void> {
    private static final String TAG="InsertTask";

    private final MainApplication myApp;
    private final NodesDBHelper helper;                           //数据库帮助器

    private final MutableLiveData<Resource<Boolean>> mRequestSchedule;

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

//        try{
//            helper.add(node);
//            mRequestSchedule.postValue(new Resource<>(
//                    true,
//                    ProcessType.insert_successful,
//                    "成功"
//            ));
//        } catch (Exception e) {
//            mRequestSchedule.postValue(new Resource<>(
//                    false,
//                    ProcessType.insert_failing,
//                    "失败"
//            ));
//        }

        long result=helper.add(node);
        if(result != -1){
            node._id= result;
            mRequestSchedule.postValue(new Resource<>(
                    true,
                    ProcessType.insert_successful,
                    "成功"
            ));
        } else {
            mRequestSchedule.postValue(new Resource<>(
                    false,
                    ProcessType.insert_failing,
                    "失败"
            ));
        }
        return null;
    }
}
