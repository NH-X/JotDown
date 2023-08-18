package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

// 该类已弃用，更好的替代方式是使用RxJava and RxAndroid
@Deprecated
public class DeleteNodeTask extends AsyncTask<Long,Void,Void> {
    private static final String TAG="DeleteTask";

    private final MainApplication myApp;
    private final NodesDBHelper helper;                       //数据库帮助器

    private MutableLiveData<Resource<Long>> mRequestSchedule;

    public DeleteNodeTask(MutableLiveData<Resource<Long>> requestSchedule){
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

        try{
            helper.deleteById(_id);
            mRequestSchedule.postValue(new Resource<>(
                    _id,
                    ProcessType.delete_successful,
                    "成功"
            ));
        } catch (Exception e) {
            mRequestSchedule.postValue(new Resource<>(
                    -1l,
                    ProcessType.delete_failing,
                    "失败"
            ));
        }

        return null;
    }
}
