package com.example.jotdown.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jotdown.R;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.utils.ColorUtil;
import com.example.jotdown.widget.RecyclerExtras;

import java.util.List;

public class LinearDynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {
    private final static String TAG="LinearDynamicAdapter";
    private Context context;                    //声明一个上下文对象

    private List<NodeInfo> nodesArray;

    public LinearDynamicAdapter(Context context, List<NodeInfo> nodesArray){
        this.context=context;
        this.nodesArray=nodesArray;
    }

    @Override
    public void onClick(View view) {
        ItemHolder itemHolder= (ItemHolder) view.getTag();
        int position=itemHolder.position;
        Log.d(TAG, "onClick: position="+position);
        if(view.getId()==R.id.ll_background) {
            onItemClickListener.onItemClick(view, position);
        }
        else if(view.getId()==R.id.iv_listen){
            Log.d(TAG, "onClick: audioFilePath is "+nodesArray.get(position).audioFilePath);
            onItemPlayListener.onItemPlayerClick(nodesArray.get(position).audioFilePath);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int position=((ItemHolder)(view.getTag())).position;
        if(onItemLongClickListener!=null){
            Log.d(TAG, "onLongClick: OnLongClick:position="+position);
            onItemLongClickListener.onItemLongClick(view,position);
        }
        return true;
    }

    //创建列表项的视图持有者
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //根据布局文件item_linear.xml生成视图对象
        View v= LayoutInflater.from(context).inflate(R.layout.item_detail,parent,false);
        return new ItemHolder(v);
    }

    //绑定列表项视图持有者
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NodeInfo item=nodesArray.get(position);
        ItemHolder itemHolder= (ItemHolder) holder;

        itemHolder.ll_background.setOnClickListener(this);
        itemHolder.ll_background.setOnLongClickListener(this);

        itemHolder.iv_mark.setImageDrawable(ColorUtil.settingPNGColor(context,
                R.drawable.ic_label,item.labelColor));
        ColorUtil.settingShapeColor(itemHolder.ll_background,new float[]{0, 0, 0, 0, 98, 98, 0, 0},item.backgroundColor);
        itemHolder.tv_title.setText(item.title);
        itemHolder.tv_title.setTextColor(item.titleColor);
        itemHolder.tv_title.setTextSize(item.titleSize);
        itemHolder.tv_content.setText(item.content);
        itemHolder.tv_content.setTextColor(item.contentColor);
        itemHolder.tv_content.setTextSize(item.contentSize);
        itemHolder.tv_remind.setText(item.remind);
        itemHolder.tv_changeDate.setText(item.changeTime);
        itemHolder.position=position;
        itemHolder.ll_background.setTag(itemHolder);
        if(!nodesArray.get(position).audioFilePath.equals("")) {
            itemHolder.iv_listen.setVisibility(View.VISIBLE);
            itemHolder.iv_listen.setOnClickListener(this);
            itemHolder.iv_listen.setTag(itemHolder);
        }
        else{
            itemHolder.iv_listen.setVisibility(View.GONE);
        }
        Log.d(TAG, "onBindViewHolder: title="+itemHolder.tv_title.getText());
    }

    //获取列表项个数
    @Override
    public int getItemCount() {
        return nodesArray==null?0:nodesArray.size();
    }

    //定义列表项的视图持有者
    private class ItemHolder extends RecyclerView.ViewHolder{
        public LinearLayout ll_item;
        public ImageView iv_mark;
        private ImageView iv_listen;
        public LinearLayout ll_background;
        public TextView tv_title;
        public TextView tv_content;
        public TextView tv_remind;
        public TextView tv_changeDate;

        public int position;                            //数组下标

        public ItemHolder(View itemView){
            super(itemView);
            ll_item=itemView.findViewById(R.id.ll_item);
            iv_mark=itemView.findViewById(R.id.iv_mark);
            iv_listen=itemView.findViewById(R.id.iv_listen);
            ll_background=itemView.findViewById(R.id.ll_background);
            tv_title =itemView.findViewById(R.id.tv_title);
            tv_content=itemView.findViewById(R.id.tv_content);
            tv_remind=itemView.findViewById(R.id.tv_remind);
            tv_changeDate=itemView.findViewById(R.id.tv_changeDate);
        }
    }

    public void dataSet(List<NodeInfo> nodesArray){
        this.nodesArray=nodesArray;
        Toast.makeText(context, "dataSet: nodesArray size:"+nodesArray.size(), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
    }

    //声明列表项的点击监听器
    private RecyclerExtras.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(RecyclerExtras.OnItemClickListener listener){
        this.onItemClickListener=listener;
    }

    //声明列表项的长按监听器对象
    private RecyclerExtras.OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(RecyclerExtras.OnItemLongClickListener listener){
        this.onItemLongClickListener=listener;
    }

    //声明列表项的删除监听器
    private RecyclerExtras.OnItemDeleteClickListener onItemDeleteClickListener;

    public void setOnItemDeleteClickListener(RecyclerExtras.OnItemDeleteClickListener listener){
        this.onItemDeleteClickListener=listener;
    }

    //声明列表项的点击监听器
    private RecyclerExtras.OnItemPlayListener onItemPlayListener;

    public void setOnItemPlayListener(RecyclerExtras.OnItemPlayListener listener){
        this.onItemPlayListener=listener;
    }
}
