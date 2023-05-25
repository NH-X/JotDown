package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.QueryTask;

import java.util.List;

public class NodesRepository {
    private static final String TAG="NodesRepository";

    private static QueryTask queryTask;

    public static NodesRepository instance;

    public static NodesRepository getInstance(){
        if(instance==null){
            instance=new NodesRepository();
        }
        return instance;
    }

    public MutableLiveData<List<NodeInfo>> getNodesArray(){
        return null;
    }

    public void startQuery(MutableLiveData<Resource<List<NodeInfo>>> mRequestSchedule){
        mRequestSchedule.postValue(new Resource<>(
                null,
                QueryProcessType.query_executing,
                "查询中"
        ));
        queryTask=new QueryTask(mRequestSchedule);
    }
}
