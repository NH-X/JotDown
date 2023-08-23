package com.example.jotdown.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.R;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.receiver.AlarmReceiver;
import com.example.jotdown.repositories.QueryNodesRepository;
import com.example.jotdown.task.DeleteAudioThread;
import com.example.jotdown.utils.CancellationNotifyUtil;
import com.example.jotdown.utils.PermissionUtil;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";

    private final Context context;
    private QueryNodesRepository nodesRepo;

    private MutableLiveData<Resource<List<NodeInfo>>> mNodeList;
    private MutableLiveData<Resource<Long>> mDeleteSchedule;

    public MainViewModel(@NonNull Application application) {
        super(application);
        context = application;
    }

    public void init() {
        if (nodesRepo != null) {
            return;
        }
        nodesRepo = QueryNodesRepository.getInstance();
        mNodeList = nodesRepo.getNodesArray();
        mDeleteSchedule = nodesRepo.getDeleteNode();
    }

    public LiveData<Resource<Long>> getDeleteSchedule() {
        return mDeleteSchedule;
    }

    public LiveData<Resource<List<NodeInfo>>> getNodeList() {
        Log.d(TAG, "getNodeList: run");
        return mNodeList;
    }

    public void deleteNode(NodeInfo info) {
        nodesRepo.startDelete(info._id);                                        //删除数据库中的数据
        if (info.audioFilePath != null && !info.audioFilePath.equals("")) {     //如果该备忘录有录音文件，删除录音文件
            new DeleteAudioThread(info.audioFilePath).start();
        }
        if (!info.remind.equals(context.getString(R.string.notRemind))) {                                   //如果该备忘录有设置提醒时间
            CancellationNotifyUtil.deleteReminder(
                    context,
                    AlarmReceiver.class,
                    info.requestCode
            );       //取消该备忘录的提醒
            Log.d(TAG, "deleteNode: unRemind");
        }
    }

    public void queryNodes(String query) {
        nodesRepo.startQuery(query);
    }

    public void onResume() {
        nodesRepo.onResume();
    }

    public void onStop() {
        nodesRepo.onStop();
    }

    public void onDestroy() {
        nodesRepo.onDestroy();
    }
}