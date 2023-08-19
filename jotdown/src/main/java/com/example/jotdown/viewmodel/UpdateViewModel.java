package com.example.jotdown.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.repositories.UpDateNodeRepository;

public class UpdateViewModel extends AndroidViewModel {
    private static final String TAG="UpdateViewModel";

    private final Context context;
    private NodesDBHelper helper;                       //数据库帮助器
    private UpDateNodeRepository updateNodeRepo;

    private MutableLiveData<Resource<Boolean>> mUpdateSchedule;
    private MutableLiveData<Resource<NodeInfo>> mQuerySchedule;

    public UpdateViewModel(@NonNull Application application) {
        super(application);
        context=application;
    }

    public void init() {
        if (updateNodeRepo == null) {
            helper = MainApplication.getInstance().getNodesDBHelper();
            updateNodeRepo = UpDateNodeRepository.getInstance();
            mUpdateSchedule = updateNodeRepo.getUpdateSchedule();
            mQuerySchedule = updateNodeRepo.getQuerySchedule();
        }
    }

    public LiveData<Resource<Boolean>> getUpdateSchedule(){
        return mUpdateSchedule;
    }

    public LiveData<Resource<NodeInfo>> getQuerySchedule(){
        return mQuerySchedule;
    }

    public void updateNode(NodeInfo node){
        updateNodeRepo.startUpdate(node);
    }

    public void queryNode(long rowId){
        updateNodeRepo.startQuery(context,rowId);
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
