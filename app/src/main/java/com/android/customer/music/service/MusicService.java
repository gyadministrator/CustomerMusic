package com.android.customer.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.android.customer.music.R;
import com.android.customer.music.activity.MainActivity;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.event.MusicEvent;
import com.android.customer.music.helper.MediaPlayerHelper;
import com.android.customer.music.model.Music;
import com.android.customer.music.utils.NotificationUtil;
import com.android.customer.music.utils.NotificationUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * 通过service连接PlayMusicView和MediaPlayerHelper
 * PlayMusicView--Service
 * 1.播放音乐，暂停音乐
 * 2.启动service，绑定service，解除绑定service
 * MediaPlayerHelper--Service
 * 1.播放音乐，暂停音乐
 * 2.监听音乐播放完成，停止service
 */
public class MusicService extends Service {
    private MediaPlayerHelper mMediaPlayerHelper;
    private Music mMusic;
    private final int NOTIFICATION_ID = 1;//不可为0
    private String CHANNEL_ONE_ID = "CHANNEL_ONE_ID";
    private String CHANNEL_ONE_NAME = "CHANNEL_ONE_ID";
    private NotificationChannel notificationChannel = null;
    private Notification notification = null;
    private Bitmap bitmap;
    private Context mContext;

    public MusicService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            if (Constants.CANCEL.equals(action)) {
                //取消
                NotificationUtils.closeNotification();
            } else if (Constants.PLAY.equals(action)) {
                //播放
                if (mMediaPlayerHelper.isPlaying()) {
                    NotificationUtils.sendCustomNotification(mContext, mMusic, bitmap, R.mipmap.play);
                    mMediaPlayerHelper.pause();
                } else {
                    NotificationUtils.sendCustomNotification(mContext, mMusic, bitmap, R.mipmap.stop);
                    mMediaPlayerHelper.start();
                }
                EventBus.getDefault().post(new MusicEvent());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public class MusicBind extends Binder {
        /**
         * 设置音乐（MusicModel）
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void setMusic(final Music music) {
            mMusic = music;
            //startForeground();
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void run() {
                    if (music.getImageUrl() != null) {
                        bitmap = returnBitmap(music.getImageUrl());
                        if (NotificationUtil.isNotificationEnabled(mContext) && bitmap != null) {
                            NotificationUtils.sendCustomNotification(mContext, mMusic, bitmap, R.mipmap.stop);
                        }
                    } else {
                        if (NotificationUtil.isNotificationEnabled(mContext)) {
                            NotificationUtils.sendCustomNotification(mContext, mMusic, bitmap, R.mipmap.stop);
                        }
                    }
                }
            }).start();
        }

        /**
         * 根据图片的url路径获得Bitmap对象
         *          *
         * @param url 图片地址
         * @return
         */
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        private Bitmap returnBitmap(String url) {
            URL fileUrl = null;
            Bitmap bitmap = null;

            try {
                fileUrl = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection conn = (HttpURLConnection) Objects.requireNonNull(fileUrl)
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        /**
         * 播放音乐
         */
        public void playMusic() {
            if (mMediaPlayerHelper.getPath() != null && mMediaPlayerHelper.getPath().equals(mMusic.getPath())) {
                mMediaPlayerHelper.start();
            } else {
                mMediaPlayerHelper.setPath(mMusic.getPath());
                mMediaPlayerHelper.setOnMediaPlayerHelperListener(new MediaPlayerHelper.OnMediaPlayerHelperListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mMediaPlayerHelper.start();
                    }

                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopSelf();
                    }
                });
            }
        }

        /**
         * 暂停音乐
         */
        public void stopMusic() {
            mMediaPlayerHelper.pause();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBind();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayerHelper = MediaPlayerHelper.getInstance(this);
        mContext = this;
    }

    /**
     * 系统默认不允许不可见的后台服务播放音乐
     * Notification
     */
    /**
     * 设置服务在前台可见
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startForeground() {
        /**
         * 通知栏点击跳转的intent
         */
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);

        /**
         * 设置notification在前台展示
         */
        //进行8.0的判断
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
            notification = new Notification.Builder(this).setChannelId(CHANNEL_ONE_ID)
                    .setTicker("Nature")
                    .setSmallIcon(R.mipmap.logo)
                    .setContentTitle(mMusic.getTitle())
                    .setContentIntent(pendingIntent)
                    .setContentText(mMusic.getAuthor())
                    .build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
        } else {
            /**
             * 创建Notification
             */
            notification = new Notification.Builder(this)
                    .setContentTitle(mMusic.getTitle())
                    .setContentText(mMusic.getAuthor())
                    .setSmallIcon(R.mipmap.logo)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(NOTIFICATION_ID, notification);
    }
}
