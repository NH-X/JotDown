package com.example.jotdown;

import android.app.Application;
import android.util.Log;

import com.example.jotdown.db.LabelDBHelper;
import com.example.jotdown.db.NodesDBHelper;

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private static MainApplication myApp;

    public static NodesDBHelper nodesDBHelper;
    public static LabelDBHelper labelDBHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        nodesDBHelper = new NodesDBHelper(this, null, null, 1);
        labelDBHelper = new LabelDBHelper(this, null, null, 1);
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

    public static LabelDBHelper getLabelDBHelper() {
        if (labelDBHelper == null) {
            labelDBHelper = new LabelDBHelper(getInstance(), null, null, 1);
        }
        return labelDBHelper;
    }
}