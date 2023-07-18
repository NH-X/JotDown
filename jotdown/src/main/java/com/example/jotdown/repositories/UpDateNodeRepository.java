package com.example.jotdown.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.QueryNodeThread;
import com.example.jotdown.task.UpdateTask;

public class UpDateNodeRepository {
    private static final String TAG="UpDateNodeRepository";

    private static UpDateNodeRepository instance;

    public static UpDateNodeRepository getInstance(){
        if(null == instance){
            instance=new UpDateNodeRepository();
        }
        return instance;
    }

    public void startUpdate(MutableLiveData<Resource<Boolean>> mRequestSchedule, NodeInfo node){
        mRequestSchedule.setValue(new Resource<>(
                false,
                ProcessType.update_executing,
                "更新中"
        ));
        new UpdateTask(mRequestSchedule).execute(node);
    }

    public void startQuery(
            Context context,
            MutableLiveData<Resource<NodeInfo>> mRequestSchedule,
            long rowId){
        mRequestSchedule.setValue(new Resource<>(
                new NodeInfo(context),
                ProcessType.query_executing,
                "查询中"
        ));
        new QueryNodeThread(rowId,mRequestSchedule).start();
    }
}
