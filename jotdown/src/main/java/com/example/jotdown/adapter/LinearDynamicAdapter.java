package com.example.jotdown.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.jotdown.R;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.widget.RecyclerExtras;

import java.util.List;

public class LinearDynamicAdapter extends Adapter<ViewHolder>
implements OnClickListener, OnLongClickListener {
    private final static String TAG = "LinearDynamicAdapter";

    private final Context context;                      // 声明一个上下文对象

    private List<NodeInfo> nodesArray;

    public LinearDynamicAdapter(Context context, List<NodeInfo> nodesArray) {
        this.context = context;
        this.nodesArray = nodesArray;
    }

    @Override
    public void onClick(View view) {
        ItemHolder itemHolder = (ItemHolder) view.getTag();
        int position = itemHolder.currentPosition;
        if (view.getId() == R.id.ll_item) {
            onItemClickListener.onItemClick(view, position);
        } else if (view.getId() == R.id.iv_listen) {
            onItemPlayListener.onItemPlayerClick(nodesArray.get(position).audioFilePath);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int position = ((ItemHolder) view.getTag()).currentPosition;
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(view, position);
        }
        return true;
    }

    // 创建列表项的视图持有者
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 根据布局文件item_linear.xml生成视图对象
        View v = LayoutInflater.from(context).inflate(R.layout.item_detail, parent, false);

        return new ItemHolder(v);
    }

    //绑定列表项视图持有者
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NodeInfo item = nodesArray.get(position);
        ItemHolder itemHolder = (ItemHolder) holder;

        itemHolder.ll_item.setOnClickListener(this);
        itemHolder.ll_item.setOnLongClickListener(this);

        if (item.title == null) {
            itemHolder.tv_title.setVisibility(View.GONE);
        } else {
            itemHolder.tv_title.setVisibility(View.VISIBLE);
            itemHolder.tv_title.setText(item.title);
        }
        if (item.content == null) {
            itemHolder.tv_content.setVisibility(View.GONE);
        } else {
            itemHolder.tv_content.setVisibility(View.VISIBLE);
            itemHolder.tv_content.setText(item.content);
        }
        if (item.remind == null || item.remind.equals(context.getString(R.string.notRemind))) {
            itemHolder.ll_remind.setVisibility(View.GONE);
        } else {
            itemHolder.ll_remind.setVisibility(View.VISIBLE);
            itemHolder.tv_remind.setText(item.remind);
        }
        itemHolder.currentPosition = position;
        itemHolder.ll_item.setTag(itemHolder);
        if (!nodesArray.get(position).audioFilePath.equals("")) {
            itemHolder.iv_listen.setVisibility(View.VISIBLE);
            itemHolder.iv_listen.setOnClickListener(this);
            itemHolder.iv_listen.setTag(itemHolder);
        } else {
            itemHolder.iv_listen.setVisibility(View.GONE);
        }

        Log.d(TAG, "onBindViewHolder: title=" + itemHolder.tv_title.getText());
    }

    @Override
    public int getItemCount() {
        return nodesArray == null ? 0 : nodesArray.size();
    }

    // 定义列表项的视图持有者
    private static class ItemHolder extends ViewHolder {
        public LinearLayout ll_item;
        public LinearLayout ll_remind;
        public ImageView iv_listen;
        public TextView tv_title;
        public TextView tv_content;
        public TextView tv_remind;
        public RecyclerView rv_label;
        public int currentPosition;                        //数组下标

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            ll_item = itemView.findViewById(R.id.ll_item);
            ll_remind = itemView.findViewById(R.id.ll_remind);
            iv_listen = itemView.findViewById(R.id.iv_listen);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_remind = itemView.findViewById(R.id.tv_remind);
            rv_label = itemView.findViewById(R.id.rv_label);
        }
    }

    public void dataSet(List<NodeInfo> nodesArray) {
        this.nodesArray = nodesArray;
        //Toast.makeText(context, "dataSet: nodesArray size:"+nodesArray.size(), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    //声明列表项的点击监听器
    private RecyclerExtras.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(RecyclerExtras.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    //声明列表项的长按监听器对象
    private RecyclerExtras.OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(RecyclerExtras.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    //声明列表项的删除监听器
    private RecyclerExtras.OnItemDeleteClickListener onItemDeleteClickListener;

    public void setOnItemDeleteClickListener(RecyclerExtras.OnItemDeleteClickListener listener) {
        this.onItemDeleteClickListener = listener;
    }

    //声明列表项的点击监听器
    private RecyclerExtras.OnItemPlayListener onItemPlayListener;

    public void setOnItemPlayListener(RecyclerExtras.OnItemPlayListener listener) {
        this.onItemPlayListener = listener;
    }
}