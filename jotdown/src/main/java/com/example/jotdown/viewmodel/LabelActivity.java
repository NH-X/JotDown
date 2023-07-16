package com.example.jotdown.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.jotdown.MainApplication;
import com.example.jotdown.db.LabelDBHelper;

public class LabelActivity extends ViewModel {
    private final static String TAG="LabelActivity";

    private LabelDBHelper helper;                           //数据库帮助器

    public void init(){
        helper= MainApplication.getInstance().getLabelDBHelper();
    }

    public void onResume(){

    }

    public void onStop(){

    }

    public void onDestroy(){
        if(helper!=null){
            helper.close();                            //关闭数据库
        }
    }
}
