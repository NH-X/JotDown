package com.example.jotdown.utils;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class CancellationNotifyUtil {
    public static void deleteReminder(Context context,Class<?> cls,int requestCode){                //取消备忘录提醒
        Intent intent=new Intent(context, cls);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_NO_CREATE);

        if(pendingIntent!=null){
            // 如果PendingIntent已经存在，则取消该PendingIntent对应的闹钟提醒
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmMgr.cancel(pendingIntent);

            // 取消PendingIntent
            pendingIntent.cancel();
        }
    }
}
