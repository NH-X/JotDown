package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.LabelBean;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.LabelDBHelper;

import java.util.ArrayList;
import java.util.List;

public class QueryLabelTask extends AsyncTask<String,Void,Void> {
    public static final String TAG = "QueryLabelTask";

    private final LabelDBHelper helper;                         //数据库帮助器

    private MutableLiveData<Resource<List<LabelBean>>> mRequestSchedule;

    public QueryLabelTask(MutableLiveData<Resource<List<LabelBean>>> requestSchedule) {
        this.mRequestSchedule = requestSchedule;
        helper = MainApplication.getInstance().getLabelDBHelper();
    }

    @Override
    protected Void doInBackground(String... strings) {
        String query = strings[0];
        List<LabelBean> labelsArray = null;

        if (!helper.readIsOpen()) {
            Log.d(TAG, "doInBackground: readDB is close");
            helper.getReadDB();
        }
        if (query == null || query.equals("")) {
            labelsArray = helper.queryInfoAll();
            if (labelsArray == null) {
                mRequestSchedule.postValue(new Resource<>(
                        labelsArray,
                        ProcessType.query_failing,
                        "查询失败"
                ));
            } else {
                mRequestSchedule.postValue(new Resource<>(
                        labelsArray,
                        ProcessType.query_successful,
                        "数据库数据条数：" + labelsArray.size()
                ));
                Log.d(TAG, "doInBackground: first is run");
            }
        } else {
            labelsArray = helper.queryLikeInfo(query);
            Log.d(TAG, "doInBackground: labelsArray is null? " + (labelsArray == null));
            if (labelsArray == null) {
                mRequestSchedule.postValue(new Resource<>(
                        new ArrayList<>(),
                        ProcessType.delete_failing,
                        "查询失败"
                ));
            } else {
                if (labelsArray.size() <= 0) {
                    mRequestSchedule.postValue(new Resource<>(
                            labelsArray,
                            ProcessType.query_successful,
                            String.format("没有\"%s\"的搜索结果。\n请尝试检查您的拼写或使用关键词进行搜索", query)
                    ));
                } else {
                    mRequestSchedule.postValue(new Resource<>(
                            labelsArray,
                            ProcessType.query_successful,
                            "数据库数据条数：" + labelsArray.size()
                    ));
                    Log.d(TAG, "doInBackground: second is run");
                }
            }
        }
        return null;
    }
}