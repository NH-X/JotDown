package com.example.jotdown.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jotdown.R;
import com.example.jotdown.bean.LabelInfo;
import com.example.jotdown.utils.ColorUtil;

import java.util.List;

public class LabelAdapter extends BaseAdapter implements OnItemClickListener {
    private static final String TAG="LabelAdapter";
    private Context context;
    private List<LabelInfo> labelArray;

    //构造器
    public LabelAdapter(Context context,List<LabelInfo> labelArray){
        this.context=context;
        this.labelArray=labelArray;
    }

    //获取元素个数
    @Override
    public int getCount() {
        return labelArray.size();
    }

    @Override
    public Object getItem(int i) {
        return labelArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view==null){
            holder=new ViewHolder();
            view=LayoutInflater.from(context).inflate(R.layout.item_label,null);
            holder.iv_label=view.findViewById(R.id.iv_icon);
            holder.tv_importance=view.findViewById(R.id.tv_text);
            view.setTag(holder);
        }
        else{
            holder= (ViewHolder) view.getTag();
        }
        holder.iv_label.setBackground(
                ColorUtil.settingPNGColor(context,R.drawable.ic_label,labelArray.get(i).labelColor));
        holder.tv_importance.setText(labelArray.get(i).importance);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }


    private class ViewHolder{
        public TextView tv_importance;
        public ImageView iv_label;
    }
}
