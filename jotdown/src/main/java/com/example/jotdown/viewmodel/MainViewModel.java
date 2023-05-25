package com.example.jotdown.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.repositories.NodesRepository;

import java.util.List;

public class MainViewModel extends ViewModel {
    private static final String TAG="MainViewModel";

    private NodesRepository nodesRepo;
    private MutableLiveData<List<NodeInfo>> nodesArray;

    public void init(){
        if(nodesRepo!=null){
            return;
        }
        nodesRepo=NodesRepository.getInstance();
        nodesArray=nodesRepo.getNodesArray();
    }
    
    public MutableLiveData<List<NodeInfo>> getNodesArray(){
        return nodesArray;
    }

    public void onResume(){

    }

    public void onStop(){

    }

    public void onDestroy(){

    }
}
