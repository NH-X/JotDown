package com.example.jotdown.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.jotdown.MainActivity;
import com.example.jotdown.R;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //从Intent中获取标题消息标题
        String title = intent.getStringExtra("title");
        // 从Intent中获取备忘录标题
        String content = intent.getStringExtra("content");// 从Intent中获取requestCode
        int requestCode = (int) intent.getLongExtra("requestCode", 0);

        // 创建PendingIntent并设置跳转的Intent
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, mainActivityIntent, 0);

        // 创建通知渠道
        String channelId = "MemoReminderChannel";
        String channelName = "Memo Reminder Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        // 发送通知
        notificationManager.notify(0, builder.build());
    }
}
