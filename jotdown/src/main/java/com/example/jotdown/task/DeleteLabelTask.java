package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.LabelDBHelper;

import java.util.List;

// 该类已弃用，更好的替代方式是使用RxJava and RxAndroid
@Deprecated
public class DeleteLabelTask extends AsyncTask<List<Long>,Void,Void> {
    public static final String TAG="DeleteLabelTask";

    private final LabelDBHelper helper;                     //数据库帮助器

    public MutableLiveData<Resource<Long>> mRequestSchedule;

    public DeleteLabelTask(MutableLiveData<Resource<Long>> requestSchedule) {
        this.helper = MainApplication.getInstance().getLabelDBHelper();
        this.mRequestSchedule=requestSchedule;
    }

    @Override
    protected Void doInBackground(List<Long>... lists) {
        List<Long> rowsId=lists[0];

        if(!helper.writeIsOpen()){
            Log.d(TAG, "doInBackground: writeDB is close");
            helper.getWriteDB();
        }
        long deleteCount=0;
        try{
            for (long _id:rowsId) {
                if(helper.deleteById(_id)!=-1){
                    deleteCount++;
                }
            }
            mRequestSchedule.postValue(new Resource<>(
                    deleteCount,
                    ProcessType.delete_successful,
                    "成功"
            ));

        } catch (Exception e) {
            mRequestSchedule.postValue(new Resource<>(
                    -1L,
                    ProcessType.delete_failing,
                    e.getMessage()
            ));
        }
        return null;
    }
}
