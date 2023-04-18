package com.example.jotdown;

import android.app.Application;
import android.util.Log;

import com.example.jotdown.bean.LabelInfo;
import com.example.jotdown.db.NodesDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static MainApplication myApp;

    private static NodesDBHelper nodesDBHelper;

    private static List<LabelInfo> labelArray;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        nodesDBHelper = new NodesDBHelper(this, null, null, 1);
        Log.d(TAG, "onCreate: nodesDBHelper is null?"+(nodesDBHelper==null));
        labelArray=new ArrayList<>();

        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.grey),"未知"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.blue),"个人"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.black),"工作"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.cyan_blue),"学习"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.red),"重要"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.red),"红色"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.green),"绿色"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.blue),"蓝色"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.purple),"紫色"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.grey),"灰色"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.yellow),"黄色"));
        labelArray.add(new LabelInfo(getInstance().getResources().getColor(R.color.orange),"橙色"));
    }

    public static MainApplication getInstance() {
        Log.d(TAG, "getInstance: myApp is null?" + (null == myApp));
        return myApp;
    }

    public static NodesDBHelper getNodesDBHelper() {
        if (nodesDBHelper == null) {
            nodesDBHelper = new NodesDBHelper(getInstance(), null, null, 1);
        }
        return nodesDBHelper;
    }

    public static List<LabelInfo> getLabelArray(){
        if(labelArray==null){
            labelArray=new ArrayList<>();
        }
        return labelArray;
    }
}