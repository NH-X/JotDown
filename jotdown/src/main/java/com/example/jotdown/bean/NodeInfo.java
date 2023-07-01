package com.example.jotdown.bean;

import android.content.Context;

import com.example.jotdown.R;
import com.example.jotdown.utils.DateUtil;

public class NodeInfo {
    public long _id;
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

    public String audioFilePath;        //音频放置位置

    public NodeInfo(Context context){
        _id=-1;
        title=null;
        titleColor=context.getResources().getColor(R.color.red);
        titleSize=35;
        content=null;
        contentColor=context.getResources().getColor(R.color.black);
        contentSize=20;
        importance="其他";
        labelColor=context.getResources().getColor(R.color.grey);
        backgroundColor= context.getResources().getColor(R.color.transparent);
        remind=context.getString(R.string.notRemind);
        requestCode= (int) (System.currentTimeMillis()%Integer.MAX_VALUE);
        changeTime= DateUtil.getNowDateTime();
        audioFilePath="";
    }
}