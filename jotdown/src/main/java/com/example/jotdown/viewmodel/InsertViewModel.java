package com.example.jotdown.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.repositories.InsertNodeRepository;

public class InsertViewModel extends ViewModel {
    private static final String TAG="InsertViewModel";

    private NodesDBHelper helper;                       //数据库帮助器
    private InsertNodeRepository insertNodeRepo;

    private MutableLiveData<Resource<Boolean>> mInsertSchedule=new MutableLiveData<>();

    public void init(){
        if(insertNodeRepo!=null){
            return;
        }
        helper= MainApplication.getInstance().getNodesDBHelper();
        insertNodeRepo= InsertNodeRepository.getInstance();
    }

    public LiveData<Resource<Boolean>> getInsertSchedule(){
        return mInsertSchedule;
    }

    public void insertNode(NodeInfo node){
        insertNodeRepo.startInsert(mInsertSchedule,node);
    }

    public void onResume(){

    }

    public void onStop(){

    }

    public void onDestroy(){
        if(helper!=null){
            helper.close();                            //关闭数据库
        }
    }
}
