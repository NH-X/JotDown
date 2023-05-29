package com.example.jotdown.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.repositories.UpDateNodeRepository;

public class UpdateViewModel extends ViewModel {
    private static final String TAG="UpdateViewModel";

    private NodesDBHelper helper;                       //数据库帮助器
    private static UpDateNodeRepository updateNodeRepo;

    private MutableLiveData<Resource<Boolean>> mUpdateSchedule =new MutableLiveData<>();

    public void init(){
        if(updateNodeRepo!=null){
            return;
        }
        helper= MainApplication.getNodesDBHelper();
        updateNodeRepo=UpDateNodeRepository.getInstance();
    }

    public LiveData<Resource<Boolean>> getUpdateSchedule(){
        return mUpdateSchedule;
    }

    public void updateNode(NodeInfo node){
        updateNodeRepo.startUpdate(mUpdateSchedule,node);
    }

    public void onResume() {

    }

    public void onStop() {

    }

    public void onDestroy() {
        if (helper != null) {
            helper.close();                         //关闭数据库
        }
    }
}
