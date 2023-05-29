package com.example.jotdown.repositories;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.QueryProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.task.UpdateTask;

public class UpDateNodeRepository {
    private static final String TAG="UpDateNodeRepository";

    private static UpdateTask updateTask;

    private static UpDateNodeRepository instance;

    public static UpDateNodeRepository getInstance(){
        if(null == instance){
            instance=new UpDateNodeRepository();
        }
        return instance;
    }

    public void startUpdate(MutableLiveData<Resource<Boolean>> mRequestSchedule, NodeInfo node){
        mRequestSchedule.setValue(new Resource<>(
                false,
                QueryProcessType.update_executing,
                "更新中"
        ));
        updateTask=new UpdateTask(mRequestSchedule);
        updateTask.execute(node);
    }
}
