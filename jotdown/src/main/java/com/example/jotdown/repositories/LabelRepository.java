package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.LabelBean;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;

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

    public void startQuery(MutableLiveData<Resource<List<LabelBean>>> mRequestSchedule,String query){
        mRequestSchedule.setValue(new Resource<>(
                new ArrayList<>(),
                QueryProcessType.query_executing,
                "查询中"
        ));

    }
}
