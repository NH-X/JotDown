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

    private static final int DATABASE_VERSION=3;
    private static NodesDBHelper nodesDBHelper;

    private static List<LabelInfo> labelArray;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        nodesDBHelper = new NodesDBHelper(this, null, null, DATABASE_VERSION);
        Log.d(TAG, "onCreate: nodesDBHelper is null?"+(nodesDBHelper==null));
        labelArray=new ArrayList<>();

        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.grey),
                getInstance().getResources().getString(R.string.unknown)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.blue),
                getInstance().getResources().getString(R.string.personal)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.black),
                getInstance().getResources().getString(R.string.work)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.cyan_blue),
                getInstance().getResources().getString(R.string.study)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.red),
                getInstance().getResources().getString(R.string.important)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.red),
                getInstance().getResources().getString(R.string.red)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.green),
                getInstance().getResources().getString(R.string.green)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.blue),
                getInstance().getResources().getString(R.string.blue)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.purple),
                getInstance().getResources().getString(R.string.purple)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.grey),
                getInstance().getResources().getString(R.string.grey)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.yellow),
                getInstance().getResources().getString(R.string.yellow)));
        labelArray.add(new LabelInfo(
                getInstance().getColor(R.color.orange),
                getInstance().getResources().getString(R.string.orange)));
    }

    public void onTerminate(){
        super.onTerminate();
        if(nodesDBHelper!=null){
            nodesDBHelper.close();
        }
    }

    public static MainApplication getInstance() {
        Log.d(TAG, "getInstance: myApp is null?" + (null == myApp));
        return myApp;
    }

    public NodesDBHelper getNodesDBHelper() {
        if (nodesDBHelper == null) {
            nodesDBHelper = new NodesDBHelper(getInstance(), null, null, DATABASE_VERSION);
        }
        return nodesDBHelper;
    }

    public List<LabelInfo> getLabelArray(){
        if(labelArray==null){
            labelArray=new ArrayList<>();
        }
        return labelArray;
    }
}