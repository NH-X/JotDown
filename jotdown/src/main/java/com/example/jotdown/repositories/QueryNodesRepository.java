package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.DeleteNodeTask;
import com.example.jotdown.task.QueryNodeTask;

import java.util.ArrayList;
import java.util.List;

public class QueryNodesRepository {
    private static final String TAG="NodesRepository";

    private final MutableLiveData<List<NodeInfo>> nodesArray=new MutableLiveData<>();

    public static QueryNodesRepository instance;

    public static QueryNodesRepository getInstance(){
        if(instance==null){
            instance=new QueryNodesRepository();
        }
        return instance;
    }

    public MutableLiveData<List<NodeInfo>> getNodesArray(){
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