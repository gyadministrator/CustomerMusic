package com.android.customer.music.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.android.customer.music.BuildConfig;
import com.android.customer.music.R;
import com.android.customer.music.activity.MainActivity;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.model.Music;
import com.android.customer.music.service.MusicService;

import static android.app.Notification.VISIBILITY_SECRET;

/**
 * Created by Administrator on 2018/3/28.
 */

public class NotificationUtils {
    private static NotificationManager notificationManager;
    private static RemoteViews remoteViews;
    private static Music mMusic;
    private final static String CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel";
    private final static String CHANNEL_NAME = BuildConfig.APPLICATION_ID + ".name";

    private static NotificationManager getManager(Context context) {
        if (notificationManager == null)
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    public static void setMusic(Music music) {
        mMusic = music;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNormalNotification(Context context) {
        Notification.Builder builder = getNotificationBuilder(context);
        getManager(context).notify(1, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static void sendCustomNotification(Context context, Music music, Bitmap bitmap, int image) {
        if (music == null) music = mMusic;
        Notification.Builder builder = getNotificationBuilder(context);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.back_view);
        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.back_music_img, bitmap);
        } else {
            remoteViews.setImageViewResource(R.id.back_music_img, R.mipmap.logo);
        }
        String title = music.getTitle();
        if (title.length() > 10) {
            title = title.substring(0, 10) + "...";
        }
        remoteViews.setTextViewText(R.id.back_music_title, title);
        remoteViews.setTextViewText(R.id.back_music_singer, music.getAuthor());
        remoteViews.setImageViewResource(R.id.back_play, image);
        PendingIntent intent = PendingIntent.getActivity(context, -1, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_back, intent);
        Intent service = new Intent(context, MusicService.class);

        //清除
        service.setAction(Constants.CANCEL);
        PendingIntent cancelIntent = PendingIntent.getService(context, 2, service, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.back_music_close, cancelIntent);
        //播放
        service.setAction(Constants.PLAY);
        PendingIntent playIntent = PendingIntent.getService(context, 2, service, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.back_play, playIntent);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            builder.setCustomContentView(remoteViews);
        } else {
            builder.setContent(remoteViews);
        }
        builder.setOngoing(true);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        getManager(context).notify(1, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Notification.Builder getNotificationBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//是否绕过请勿打扰模式
            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//指定闪光时灯光的颜色
            channel.canShowBadge();//桌面launcher消息角标
            channel.enableVibration(false);//是否允许震动
            channel.getGroup();//获取通知渠道组
            channel.setBypassDnd(true);//设置可以绕过打扰模式
            channel.setVibrationPattern(new long[]{0});//震动的模式
            channel.shouldShowLights();//是否会出灯光
            channel.setSound(null, null);

            getManager(context).createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(context);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setOnlyAlertOnce(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        builder.setSmallIcon(R.mipmap.logo);
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ObsoleteSdkInt")
    public static void showNotification(Context context, Music music) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.logo);
        builder.setContentTitle(music.getTitle());
        builder.setContentText(music.getAuthor());
        // 需要VIBRATE权限
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setOngoing(true);
        NotificationChannel channel = new NotificationChannel("1", "channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);//是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.GREEN);//小红点颜色
        channel.setShowBadge(true);//是否在久按桌面图标时显示此渠道的通知
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
        assert notificationManager != null;
        notificationManager.notify(100, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void closeNotification() {
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.deleteNotificationChannel(CHANNEL_ID);
            }
            notificationManager.cancel(1);
        }
    }
}
