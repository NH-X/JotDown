package com.example.jotdown.task;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

import java.util.List;

public class QueryTask extends AsyncTask<String,Void,Void> {
    private static final String TAG="QueryTask";

    private MainApplication myApp;
    private NodesDBHelper helper;                       //数据库帮助器

    private MutableLiveData<Resource<List<NodeInfo>>> mRequestSchedule;

    public QueryTask(MutableLiveData<Resource<List<NodeInfo>>> requestSchedule){
        this.myApp=MainApplication.getInstance();
        this.mRequestSchedule=requestSchedule;
        helper= myApp.getNodesDBHelper();
    }

    @Override
    protected Void doInBackground(String... strings) {
        String query=strings[0];
        List<NodeInfo> nodesArray=null;

        if(!helper.readIsOpen()){
            Log.d(TAG, "doInBackground: readDB is close");
            helper.getReadDB();
        }
        if(query==null || query.equals("")){
            nodesArray=helper.queryInfoAll();
            if(nodesArray==null){
                mRequestSchedule.postValue(new Resource<>(
                        nodesArray,
                        QueryProcessType.query_failing,
                        "查询失败"
                ));
            }
            else {
                mRequestSchedule.postValue(new Resource<>(
                        nodesArray,
                        QueryProcessType.query_successful,
                        "数据库数据条数：" + nodesArray.size()
                ));
                Log.d(TAG, "doInBackground: first is run");
            }
        }
        else{
            nodesArray=helper.queryLikeInfo(query);
            Log.d(TAG, "doInBackground: nodesArray is null? "+(nodesArray==null));
            if(nodesArray==null){
                mRequestSchedule.postValue(new Resource<>(
                        null,
                        QueryProcessType.query_failing,
                        "查询失败"
                ));
            }
            else {
                if(nodesArray.size()<=0){
                    mRequestSchedule.postValue(new Resource<>(
                            nodesArray,
                            QueryProcessType.query_successful,
                            String.format("没有\"%s\"的搜索结果。\n请尝试检查您的拼写或使用关键词进行搜索",query)
                    ));
                }
                else {
                    mRequestSchedule.postValue(new Resource<>(
                            nodesArray,
                            QueryProcessType.query_successful,
                            "数据库数据条数：" + nodesArray.size()
                    ));
                    Log.d(TAG, "doInBackground: second is run");
                }
            }
        }
        return null;
    }
}
