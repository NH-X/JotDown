package com.example.jotdown.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.repositories.QueryNodesRepository;

import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG="MainViewModel";

    private MainApplication myApp;
    private NodesDBHelper helper;               //数据库帮助器
    private QueryNodesRepository nodesRepo;
    private MutableLiveData<List<NodeInfo>> nodesArray;

    private MutableLiveData<Resource<List<NodeInfo>>> mQueryAllSchedule=new MutableLiveData<>();
    private MutableLiveData<Resource<Integer>> mDeleteSchedule =new MutableLiveData<>();

    public void init(){
        if(nodesRepo!=null){
            return;
        }
        myApp=MainApplication.getInstance();
        helper= myApp.getNodesDBHelper();
        nodesRepo= QueryNodesRepository.getInstance();
        nodesArray=nodesRepo.getNodesArray();
    }
    
    public LiveData<List<NodeInfo>> getNodesArray(){
        return nodesArray;
    }

    public LiveData<Resource<Integer>> getDeleteSchedule(){
        return mDeleteSchedule;
    }

    public LiveData<Resource<List<NodeInfo>>> getQueryAllSchedule(){
        return mQueryAllSchedule;
    }

    public void deleteNode(int rowId){
        nodesRepo.startDelete(mDeleteSchedule,rowId);
    }

    public void queryNodes(String query){
        nodesRepo.startQuery(mQueryAllSchedule,query);
    }

    public void onResume(){
        nodesRepo.startQuery(mQueryAllSchedule,"");
    }

    public void onStop(){

    }

    public void onDestroy(){
        if(helper!=null){
            helper.close();                         //关闭数据库
        }
    }
}
