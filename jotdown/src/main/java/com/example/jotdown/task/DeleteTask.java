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

    private MutableLiveData<Resource<Boolean>> mRequestSchedule;

    public DeleteTask(MutableLiveData<Resource<Boolean>> requestSchedule){
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
        helper.deleteById(_id);
        mRequestSchedule.postValue(new Resource<>(
                true,
                QueryProcessType.query_successful,
                "成功"
        ));
        return null;
    }
}
