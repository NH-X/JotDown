package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.DeleteTask;
import com.example.jotdown.task.QueryTask;

import java.util.List;

public class QueryNodesRepository {
    private static final String TAG="NodesRepository";

    private MutableLiveData<List<NodeInfo>> nodesArray=new MutableLiveData<>();

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

    public void startQuery(MutableLiveData<Resource<List<NodeInfo>>> mRequestSchedule,String query){
        mRequestSchedule.setValue(new Resource<>(
                null,
                QueryProcessType.query_executing,
                "查询中"
        ));
        new QueryTask(mRequestSchedule).execute(query);
    }

    public void startDelete(MutableLiveData<Resource<Long>> mRequestSchedule,long rowId){
        mRequestSchedule.setValue(new Resource<>(
                -1l,
                QueryProcessType.delete_executing,
                "删除中"
        ));
        new DeleteTask(mRequestSchedule).execute(rowId);
    }
}