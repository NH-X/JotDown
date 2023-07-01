package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

public class DeleteTask extends AsyncTask<Long,Void,Void> {
    private static final String TAG="DeleteTask";

    private final MainApplication myApp;
    private final NodesDBHelper helper;                       //数据库帮助器

    private MutableLiveData<Resource<Long>> mRequestSchedule;

    public DeleteTask(MutableLiveData<Resource<Long>> requestSchedule){
        this.myApp=MainApplication.getInstance();
        this.mRequestSchedule=requestSchedule;
        helper= myApp.getNodesDBHelper();
    }

    @Override
    protected Void doInBackground(Long... integers) {
        long _id=integers[0];

        if(!helper.writeIsOpen()){
            Log.d(TAG, "doInBackground: writeDB is close");
            helper.getWriteDB();
        }

        if(helper.deleteById(_id)!=-1) {
            mRequestSchedule.postValue(new Resource<>(
                    _id,
                    QueryProcessType.delete_successful,
                    "成功"
            ));
        }
        else {
            mRequestSchedule.postValue(new Resource<>(
                    -1l,
                    QueryProcessType.delete_failing,
                    "失败"
            ));
        }
        return null;
    }
}
