package com.example.jotdown.bean;

import android.content.Context;

import com.example.jotdown.R;

public class LabelInfo {
    public int _id;
    public int labelColor;
    public String importance;

    public LabelInfo(Context context) {
        _id=-1;
        labelColor = context.getColor(R.color.blue);
        importance = "个人";
    }

    public LabelInfo(int color,String importance){
        labelColor=color;
        this.importance=importance;
    }
}