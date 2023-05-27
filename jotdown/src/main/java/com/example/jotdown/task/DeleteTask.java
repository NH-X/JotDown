package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

public class DeleteTask extends AsyncTask<Integer,Void,Void> {
    private static final String TAG="DeleteTask";

    private NodesDBHelper helper;                       //数据库帮助器

    private MutableLiveData<Resource<Integer>> mRequestSchedule;

    public DeleteTask(MutableLiveData<Resource<Integer>> requestSchedule){
        this.mRequestSchedule=requestSchedule;
        helper= MainApplication.getNodesDBHelper();
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        int _id=integers[0];

        if(!helper.writeIsOpen()){
            Log.d(TAG, "doInBackground: writeDB is close");
            helper.getWriteDB();
        }
        mRequestSchedule.postValue(new Resource<>(
                helper.deleteById(_id),
                QueryProcessType.query_successful,
                "成功"
        ));
        return null;
    }
}
