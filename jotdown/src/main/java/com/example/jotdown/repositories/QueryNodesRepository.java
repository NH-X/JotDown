package com.example.jotdown.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.jotdown.MainApplication;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.bean.ProcessType;
import com.example.jotdown.bean.Resource;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.task.DeleteNodeTask;
import com.example.jotdown.task.QueryNodeTask;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QueryNodesRepository {
    private static final String TAG="NodesRepository";

    private final MutableLiveData<Resource<List<NodeInfo>>> nodesArray = new MutableLiveData<>();
    private final MutableLiveData<Resource<Long>> deleteNode = new MutableLiveData<>();

    public static QueryNodesRepository instance;
    private static NodesDBHelper mNodesDBHelper;

    public static QueryNodesRepository getInstance(){
        if(instance==null){
            instance=new QueryNodesRepository();
            mNodesDBHelper = MainApplication.getInstance().getNodesDBHelper();
        }
        return instance;
    }

    public MutableLiveData<Resource<List<NodeInfo>>> getNodesArray(){
        return nodesArray;
    }

    public MutableLiveData<Resource<Long>> getDeleteNode(){
        return deleteNode;
    }

    // 此方法已弃用
    @Deprecated
    public void startQuery(MutableLiveData<Resource<List<NodeInfo>>> requestSchedule, String query){
        requestSchedule.setValue(new Resource<>(
                new ArrayList<>(),
                ProcessType.query_executing,
                "查询中"
        ));
        new QueryNodeTask(requestSchedule).execute(query);
    }

    public void startQuery(String query){
        if (!mNodesDBHelper.readIsOpen()){
            mNodesDBHelper.getReadDB();
        }
        Single<Resource<List<NodeInfo>>> single = Single.create(emitter -> {
            if(query == null || query.equals("")){
                Log.d(TAG, "startQuery: query all");
                // 这里执行异步操作，比如查询数据库或者从网络获取数据
                try {
                    List<NodeInfo> data = mNodesDBHelper.queryInfoAll();                // 查询数据库的操作
                    Log.d(TAG, "subscribe: query dataset has "+data.size());
                    Resource<List<NodeInfo>> resource = new Resource<>(
                            data,
                            ProcessType.query_successful,
                            "查询成功"
                    );
                    emitter.onSuccess(resource);                                        // 发射成功的数据
                } catch (Exception e) {
                    emitter.onError(e);                                                 // 发射错误
                }
            }
            else {
                Log.d(TAG, "startQuery: query title like is "+query);
                // 这里执行异步操作，比如查询数据库或者从网络获取数据
                try {
                    List<NodeInfo> data = mNodesDBHelper.queryLikeInfo(query); // 假设这是查询数据库的操作
                    Log.d(TAG, "subscribe: query dataset has "+data.size());
                    Resource<List<NodeInfo>> resource = new Resource<>(
                            data,
                            ProcessType.query_successful,
                            "查询成功"
                    );
                    emitter.onSuccess(resource); // 发射成功的数据
                } catch (Exception e) {
                    emitter.onError(e); // 发射错误
                }
            }
        });
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Resource<List<NodeInfo>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Resource<List<NodeInfo>> listResource) {
                        nodesArray.postValue(listResource);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        nodesArray.postValue(new Resource<>(
                                new ArrayList<>(),
                                ProcessType.query_failing,
                                "查询失败"
                        ));
                    }
                });
    }

    public void startDelete(MutableLiveData<Resource<Long>> requestSchedule, long rowId){
        requestSchedule.setValue(new Resource<>(
                -1L,
                ProcessType.delete_executing,
                "删除中"
        ));
        new DeleteNodeTask(requestSchedule).execute(rowId);
    }

    public void startDelete(long rowId){
        if(!mNodesDBHelper.writeIsOpen()){
            mNodesDBHelper.getWriteDB();
        }
        Single<Resource<Long>> single = Single.create(emitter -> {
            try{
                mNodesDBHelper.deleteById(rowId);
                emitter.onSuccess(new Resource<>(
                        rowId,
                        ProcessType.delete_successful,
                        "删除成功"
                ));
            }
            catch (Exception e){
                emitter.onError(e);
            }
        });
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Resource<Long>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Resource<Long> longResource) {
                        deleteNode.postValue(longResource);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        deleteNode.postValue(new Resource<>(
                                -1L,
                                ProcessType.delete_failing,
                                e.getMessage()
                        ));
                    }
                });
    }


    public void onCreate(){

    }

    public void onResume() {
        startQuery(null);
    }

    public void onStop() {

    }

    public void onDestroy() {
        if (mNodesDBHelper != null) {
            mNodesDBHelper.close();                         //关闭数据库
        }
    }
}