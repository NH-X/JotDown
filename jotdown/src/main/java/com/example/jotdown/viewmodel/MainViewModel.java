package com.example.jotdown.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.repositories.NodesRepository;

import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG="MainViewModel";

    private NodesRepository nodesRepo;
    private MutableLiveData<List<NodeInfo>> nodesArray;

    private MutableLiveData<Resource<List<NodeInfo>>> mQueryAllSchedule=new MutableLiveData<>();
    private MutableLiveData<Resource<Boolean>> mDeleteSchedule =new MutableLiveData<>();

    public void init(){
        if(nodesRepo!=null){
            return;
        }
        nodesRepo=NodesRepository.getInstance();
        nodesArray=nodesRepo.getNodesArray();
    }
    
    public LiveData<List<NodeInfo>> getNodesArray(){
        return nodesArray;
    }

    public LiveData<Resource<Boolean>> getDeleteSchedule(){
        return mDeleteSchedule;
    }

    public LiveData<Resource<List<NodeInfo>>> getQueryAllSchedule(){
        return mQueryAllSchedule;
    }

    public void deleteNode(int rowId){
        nodesRepo.startDelete(mDeleteSchedule,rowId);
    }

    public void onResume(){
        nodesRepo.startQuery(mQueryAllSchedule,"");
    }

    public void onStop(){

    }

    public void onDestroy(){

    }
}
