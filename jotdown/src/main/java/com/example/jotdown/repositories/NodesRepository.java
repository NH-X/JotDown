package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.DeleteTask;
import com.example.jotdown.task.QueryTask;

import java.util.List;

public class NodesRepository {
    private static final String TAG="NodesRepository";

    private static QueryTask queryTask;
    private static DeleteTask deleteTask;

    private MutableLiveData<List<NodeInfo>> nodesArray=new MutableLiveData<>();

    public static NodesRepository instance;

    public static NodesRepository getInstance(){
        if(instance==null){
            instance=new NodesRepository();
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
        queryTask=new QueryTask(mRequestSchedule);
        queryTask.execute(query);
    }

    public void startDelete(MutableLiveData<Resource<Integer>> mRequestSchedule,int rowId){
        mRequestSchedule.setValue(new Resource<>(
                -1,
                QueryProcessType.query_executing,
                "删除中"
        ));
        deleteTask=new DeleteTask(mRequestSchedule);
        deleteTask.execute(rowId);
    }
}