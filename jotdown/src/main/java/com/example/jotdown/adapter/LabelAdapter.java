package com.example.jotdown.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.jotdown.R;
import com.example.jotdown.bean.LabelBean;

import java.util.List;

public class LabelAdapter extends Adapter<ViewHolder> {
    private static final String TAG = "LabelAdapter";
    private static final int LABEL_MAX_COUNT=3;

    private final Context context;
    private List<LabelBean> labelArray;
    private final int count;

    public LabelAdapter(Context context, List<LabelBean> labelArray) {
        this.context = context;
        this.labelArray = labelArray;
        this.count = labelArray == null ? 0 : labelArray.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_label1, parent, false);

        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LabelBean item = labelArray.get(position);
        ItemHolder itemHolder = (ItemHolder) holder;
        if(position==LABEL_MAX_COUNT && count>LABEL_MAX_COUNT){
            itemHolder.tv_label.setText(String.format("+%d",count-LABEL_MAX_COUNT+1));
        }
        else{
            itemHolder.tv_label.setText(item.importance);
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(count, LABEL_MAX_COUNT);
    }

    private class ItemHolder extends ViewHolder {
        public TextView tv_label;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tv_label = itemView.findViewById(R.id.tv_label);
        }
    }
}