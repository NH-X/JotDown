package com.example.jotdown.bean;

import android.content.Context;

import com.example.jotdown.R;
import com.example.jotdown.utils.DateUtil;

public class NodeInfo {
    public long _id;
    public String title;                //标题
    public int titleSize;               //标题文字大小
    public String content;              //内容
    public int contentSize;             //内容文字大小

    public String importance;           //重要性
    public int labelColor;              //标签颜色

    public String remind;               //闹钟提醒
    public int requestCode;             //请求唯一标识符

    public String audioFilePath;        //音频放置位置

    public NodeInfo(Context context) {
        _id = -1;
        title = null;
        titleSize = 35;
        content = null;
        contentSize = 20;
        importance = "其他";
        labelColor = 0xD1D1D1;
        remind = context.getString(R.string.notRemind);
        requestCode = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        audioFilePath = "";
    }
}