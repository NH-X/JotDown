package com.example.jotdown.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.example.jotdown.MainApplication;
import com.example.jotdown.R;
import com.example.jotdown.bean.NodeInfo;
import com.example.jotdown.db.NodesDBHelper;
import com.example.jotdown.service.ReminderService;
import com.example.jotdown.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class SampleBootReceiver extends BroadcastReceiver {
    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            this.context=context;
            handler.post(runnable);
        }
    }

    private void resetAlarm(List<NodeInfo> nodes){
        //为每个备忘录重新创建闹钟
        for(NodeInfo node:nodes) {
            if (DateUtil.timeToTimestamp("yyyy-MM-dd HH:mm", node.remind) > System.currentTimeMillis()) {
                Intent serviceIntent = new Intent(context, ReminderService.class);
                serviceIntent.putExtra("content", node.content);
                serviceIntent.putExtra("title", context.getString(R.string.app_name));
                serviceIntent.putExtra("remindTime", DateUtil.timeToTimestamp("yyyy-MM-dd HH:mm", node.remind));
                serviceIntent.putExtra("requestCode", node.requestCode);
                context.startService(serviceIntent);
            }
        }
    }

    //private List<NodeInfo> nodes=new ArrayList<>();
    private Handler handler=new Handler();              //创建一个处理器对象
    private NodesDBHelper helper= MainApplication.getNodesDBHelper();

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            resetAlarm(helper.queryInfoBy(
                    String.format("where remind is not %s;",context.getString(R.string.notRemind))));
        }
    };
}
