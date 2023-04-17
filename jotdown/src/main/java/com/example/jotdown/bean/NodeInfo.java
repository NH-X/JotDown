package com.example.jotdown.bean;

import android.content.Context;

import com.example.jotdown.R;
import com.example.jotdown.utils.DateUtil;

public class NodeInfo {
    public int _id;
    public String title;                //标题
    public int titleColor;              //标题文字颜色
    public int titleSize;               //标题文字大小
    public String content;              //内容
    public int contentColor;            //内容文字颜色
    public int contentSize;             //内容文字大小

    public String importance;           //重要性
    public int labelColor;              //标签颜色
    public int backgroundColor;         //背景颜色

    public String remind;               //闹钟提醒
    public int requestCode;             //请求唯一标识符
    public String changeTime;           //最后更改事件

    public NodeInfo(Context context){
        _id=-1;
        title="";
        titleColor=R.color.red;
        titleSize=35;
        content="";
        contentColor=R.color.black;
        contentSize=20;
        importance="";
        labelColor=R.color.red;
        backgroundColor= R.color.transparent;
        remind=context.getString(R.string.notRemind);
        requestCode= (int) (System.currentTimeMillis()%Integer.MAX_VALUE);
        changeTime= DateUtil.getNowDateTime();
    }
}