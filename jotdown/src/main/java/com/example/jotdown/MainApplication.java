package com.example.jotdown;

import android.app.Application;
import android.util.Log;

import com.example.jotdown.bean.LabelInfo;
import com.example.jotdown.db.LabelDBHelper;
import com.example.jotdown.db.NodesDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static MainApplication myApp;

    private static final String NODE_DATABASE_NAME="nodes.sqlite";
    private static final String LABEL_DATABASE_NAME="label.sqlite";
    private static final int NODE_DATABASE_VERSION =3;
    private static final String NODE_TABLE_NAME="Nodes";
    private static final int LABEL_DATABASE_VERSION = 1;
    private static final String LABEL_TABLE_NAME="Labels";

    private static NodesDBHelper nodesDBHelper;
    private static LabelDBHelper labelDBHelper;

    private static List<LabelInfo> labelArray;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        nodesDBHelper = new NodesDBHelper(
                this,
                NODE_DATABASE_NAME,
                null,
                NODE_DATABASE_VERSION
        );
        labelDBHelper=new LabelDBHelper(
                this,
                LABEL_DATABASE_NAME,
                null,
                LABEL_DATABASE_VERSION);
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
            nodesDBHelper = new NodesDBHelper(
                    this,
                    NODE_DATABASE_NAME,
                    null,
                    NODE_DATABASE_VERSION
            );
        }
        return nodesDBHelper;
    }

    public LabelDBHelper getLabelDBHelper(){
        if(labelDBHelper == null){
            labelDBHelper=new LabelDBHelper(
                    this,
                    LABEL_DATABASE_NAME,
                    null,
                    LABEL_DATABASE_VERSION);
        }
        return labelDBHelper;
    }

    public List<LabelInfo> getLabelArray(){
        if(labelArray==null){
            labelArray=new ArrayList<>();
        }
        return labelArray;
    }

    public String getNodeTableName(){
        Log.d(TAG, "getNodeTableName: NodeTableName is "+NODE_TABLE_NAME);
        return NODE_TABLE_NAME;
    }

    public String getLabelTableName(){
        return LABEL_TABLE_NAME;
    }
}