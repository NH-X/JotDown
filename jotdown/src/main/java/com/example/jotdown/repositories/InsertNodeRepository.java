package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.InsertTask;

public class InsertNodeRepository {
    private static final String TAG="InsertNodeRepository";

    private static InsertNodeRepository instance;

    public static InsertNodeRepository getInstance(){
        if(null == instance){
            instance=new InsertNodeRepository();
        }
        return instance;
    }

    public void startInsert(MutableLiveData<Resource<Boolean>> mRequestSchedule, NodeInfo node){
        mRequestSchedule.setValue(new Resource<>(
                false,
                QueryProcessType.insert_executing,
                "插入中"
        ));
        new InsertTask(mRequestSchedule).execute(node);
    }
}
