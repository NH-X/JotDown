package com.example.jotdown;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.jotdown.db.NodesDBHelper;

public class MainApplication extends Application {
    private static final String TAG="MainApplication";
    private static MainApplication myApp;

    public static NodesDBHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp=this;
        helper=new NodesDBHelper(this,null,null,1);
    }

    public static MainApplication getInstance(){
        Log.d(TAG, "getInstance: myApp is null?"+(null==myApp));
        return myApp;
    }

    public static NodesDBHelper getNodesDBHelper(){
        if(helper==null){
            helper=new NodesDBHelper(getInstance(),null,null,1);
        }
        return helper;
    }
}