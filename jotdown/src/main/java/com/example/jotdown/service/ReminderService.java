package com.example.jotdown.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.jotdown.receiver.AlarmReceiver;

public class ReminderService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 从Intent中获取备忘录内容和提醒时间
        String content = intent.getStringExtra("content");
        String title = intent.getStringExtra("title");
        long reminderTime = intent.getLongExtra("reminderTime", 0);
        int requestCode = intent.getIntExtra("requestCode",0);

        // 创建闹钟广播Intent，设置唯一的requestCode
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("content", content);
        alarmIntent.putExtra("title",title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, 0);

        // 获取闹钟管理器
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 设置闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
        }

        // 停止服务
        stopSelf();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

//public class ReminderService extends Service {
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // 从Intent中获取备忘录内容和提醒时间
//        String memo = intent.getStringExtra("memo");
//        long reminderTime = intent.getLongExtra("reminderTime", 0);
//
//        // 创建闹钟广播Intent
//        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
//        alarmIntent.putExtra("memo", memo);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
//
//        // 获取闹钟管理器
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//
//        // 设置闹钟
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
//        } else {
//            alarmManager.set(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
//        }
//
//        // 停止服务
//        stopSelf();
//
//        return START_NOT_STICKY;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}
