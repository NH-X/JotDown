package com.example.jotdown.repositories;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.task.UpdateTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpDateNodeRepository {
    private static final String TAG = "UpDateNodeRepository";

    private MutableLiveData<Resource<Boolean>> mUpdateSchedule = new MutableLiveData<>();
    private MutableLiveData<Resource<NodeInfo>> mQuerySchedule = new MutableLiveData<>();

    private static UpDateNodeRepository instance;
    private static NodesDBHelper mNodesDBHelper;

    public static UpDateNodeRepository getInstance() {
        if (null == instance) {
            instance = new UpDateNodeRepository();
            mNodesDBHelper = MainApplication.getInstance().getNodesDBHelper();
        }
        return instance;
    }

    public MutableLiveData<Resource<Boolean>> getUpdateSchedule(){
        return mUpdateSchedule;
    }

    public MutableLiveData<Resource<NodeInfo>> getQuerySchedule(){
        return mQuerySchedule;
    }

    // 此方法已弃用
    public void startUpdate(MutableLiveData<Resource<Boolean>> mRequestSchedule, NodeInfo node) {
        mRequestSchedule.setValue(new Resource<>(
                false,
                ProcessType.update_executing,
                "更新中"
        ));
        new UpdateTask(mRequestSchedule).execute(node);
    }

    public void startUpdate(NodeInfo node) {
        Single<Resource<Boolean>> single = Single.create(emitter -> {
            if (!mNodesDBHelper.writeIsOpen()){
                mNodesDBHelper.getWriteDB();
            }
            try {
                mNodesDBHelper.update(node);
                emitter.onSuccess(new Resource<>(
                        true,
                        ProcessType.update_successful,
                        "更新成功"
                ));
            }
            catch (Exception e){
                emitter.onError(e);
            }
        });
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Resource<Boolean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Resource<Boolean> booleanResource) {
                        mUpdateSchedule.postValue(booleanResource);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mUpdateSchedule.postValue(new Resource<>(
                                false,
                                ProcessType.update_failing,
                                e.getMessage()
                        ));
                    }
                });
    }

    public void startQuery(Context context, long rowId) {
        if (!mNodesDBHelper.readIsOpen()){
            mNodesDBHelper.getReadDB();
        }
        Single<Resource<NodeInfo>> single = Single.create(emitter -> {
            NodeInfo node = mNodesDBHelper.queryInfoById(rowId).get(0);
            if (node != null){
                emitter.onSuccess(new Resource<>(
                        node,
                        ProcessType.query_successful,
                        "查询成功"
                ));
                return;
            }
            emitter.onError(new Throwable("查询失败"));
        });
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Resource<NodeInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Resource<NodeInfo> nodeInfoResource) {
                        mQuerySchedule.postValue(nodeInfoResource);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mQuerySchedule.postValue(new Resource<>(
                                new NodeInfo(context),
                                ProcessType.query_failing,
                                e.getMessage()
                        ));
                    }
                });

        /**
         * @author NH-X
         * 已弃用
         */
//        mQuerySchedule.postValue(new Resource<>(
//                new NodeInfo(context),
//                ProcessType.query_executing,
//                "查询中"
//        ));
//        new QueryNodeThread(rowId, mQuerySchedule).start();
    }
}