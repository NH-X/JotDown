package com.example.jotdown.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;              //空白间隔

    public SpacesItemDecoration(int space){
        this.space=space;
    }

    public void getItemOffsets(Rect outRect, View view,RecyclerView parent,RecyclerView.State state){
        outRect.bottom=space;                   //下方空白间隔
    }
}
