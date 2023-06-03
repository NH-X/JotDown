package com.example.jotdown.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;

public class InsertViewModel extends ViewModel {
    private static final String TAG="InsertViewModel";

    private MainApplication myApp;
    private NodesDBHelper helper;                       //数据库帮助器

    private MutableLiveData<Resource<Boolean>> mInsertSchedule=new MutableLiveData<>();

    public void init(){
        myApp=MainApplication.getInstance();
        helper= myApp.getNodesDBHelper();
    }
}
