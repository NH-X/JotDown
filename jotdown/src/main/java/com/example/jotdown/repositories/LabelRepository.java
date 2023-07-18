package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.LabelBean;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.DeleteLabelTask;
import com.example.jotdown.task.QueryLabelTask;

import java.util.ArrayList;
import java.util.List;

public class LabelRepository {
    private static final String TAG="LabelRepository";

    private final MutableLiveData<List<LabelBean>> labelsArray=new MutableLiveData<>();

    private static LabelRepository instance;

    public static LabelRepository getInstance(){
        if(null == instance){
            instance=new LabelRepository();
        }
        return instance;
    }

    public void startQuery(MutableLiveData<Resource<List<LabelBean>>> requestSchedule, String query){
        requestSchedule.setValue(new Resource<>(
                new ArrayList<>(),
                ProcessType.query_executing,
                "查询中"
        ));
        new QueryLabelTask(requestSchedule).execute(query);
    }

    public void startDelete(MutableLiveData<Resource<Long>> requestSchedule,List<Long> rowsId){
        requestSchedule.setValue(new Resource<>(
                0L,
                ProcessType.delete_executing,
                "删除中"
        ));
        new DeleteLabelTask(requestSchedule).execute(rowsId);
    }
}
