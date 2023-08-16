package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.task.DeleteNodeTask;
import com.example.jotdown.task.QueryNodeTask;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QueryNodesRepository {
    private static final String TAG="NodesRepository";

    private final MutableLiveData<Resource<List<NodeInfo>>> nodesArray = new MutableLiveData<>();

    public static QueryNodesRepository instance;
    private static NodesDBHelper mNodesDBHelper;

    public static QueryNodesRepository getInstance(){
        if(instance==null){
            instance=new QueryNodesRepository();
            mNodesDBHelper = MainApplication.getInstance().getNodesDBHelper();
        }
        return instance;
    }

    public MutableLiveData<Resource<List<NodeInfo>>> getNodesArray(){
        return nodesArray;
    }

    public void startQuery(MutableLiveData<Resource<List<NodeInfo>>> requestSchedule, String query){
        requestSchedule.setValue(new Resource<>(
                new ArrayList<>(),
                ProcessType.query_executing,
                "查询中"
        ));
        new QueryNodeTask(requestSchedule).execute(query);
    }

    public void startDelete(MutableLiveData<Resource<Long>> requestSchedule, long rowId){
        requestSchedule.setValue(new Resource<>(
                -1L,
                ProcessType.delete_executing,
                "删除中"
        ));
        new DeleteNodeTask(requestSchedule).execute(rowId);
    }
}