package com.example.jotdown.viewmodel;

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

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";

    private final Context context;
    private NodesDBHelper helper;               //数据库帮助器
    private QueryNodesRepository nodesRepo;

    private MutableLiveData<Resource<List<NodeInfo>>> mNodeList =new MutableLiveData<>();
    private MutableLiveData<Resource<Long>> mDeleteSchedule = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        context = application;
    }

    public void init() {
        if (nodesRepo != null) {
            return;
        }
        helper = MainApplication.getInstance().getNodesDBHelper();
        nodesRepo = QueryNodesRepository.getInstance();
        mNodeList = nodesRepo.getNodesArray();
    }

    public LiveData<Resource<Long>> getDeleteSchedule() {
        return mDeleteSchedule;
    }

    public LiveData<Resource<List<NodeInfo>>> getNodeList(){
        return mNodeList;
    }

    public void deleteNode(NodeInfo info) {
        nodesRepo.startDelete(mDeleteSchedule, info._id);                        //删除数据库中的数据
        if (info.audioFilePath != null && !info.audioFilePath.equals("")) {     //如果该备忘录有录音文件，删除录音文件
            new DeleteAudioThread(info.audioFilePath).start();
        }
        if (!info.remind.equals(context.getString(R.string.notRemind))) {       //如果该备忘录有设置提醒时间
            CancellationNotifyUtil.deleteReminder(
                    context,
                    AlarmReceiver.class,
                    info.requestCode
            );       //取消该备忘录的提醒
            Log.d(TAG, "deleteNode: unRemind");
        }
    }

    public void queryNodes(String query) {
        nodesRepo.startQuery(mNodeList, query);
        // nodesRepo.tmp(query);
    }

    public void onCreate(){

    }

    public void onResume() {
        nodesRepo.startQuery(mNodeList, null);
        // nodesRepo.tmp(null);
    }

    public void onStop() {

    }

    public void onDestroy() {
        if (helper != null) {
            helper.close();                         //关闭数据库
        }
    }
}