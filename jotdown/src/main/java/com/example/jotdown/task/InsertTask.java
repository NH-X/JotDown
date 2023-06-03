package com.example.jotdown.task;

import android.os.AsyncTask;

import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.db.NodesDBHelper;

public class InsertTask extends AsyncTask<NodeInfo,Void,Void> {
    private static final String TAG="InsertTask";

    private NodesDBHelper helper;

    @Override
    protected Void doInBackground(NodeInfo... nodeInfos) {
        return null;
    }
}
