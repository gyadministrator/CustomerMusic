package com.android.customer.music.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

import com.android.customer.music.R;
import com.android.customer.music.activity.MainActivity;
import com.android.customer.music.helper.MediaPlayerHelper;
import com.android.customer.music.model.Music;
import com.android.customer.music.model.MusicModel;

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

    public MusicService() {
    }

    public class MusicBind extends Binder {
        /**
         * 设置音乐（MusicModel）
         */
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void setMusic(Music music) {
            mMusic = music;
            startForeground();
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
         * 创建Notification
         */
        Notification notification = new Notification.Builder(this)
                .setContentTitle(mMusic.getTitle())
                .setContentText(mMusic.getAuthor())
                .setSmallIcon(R.mipmap.logo)
                .setContentIntent(pendingIntent)
                .build();

        /**
         * 设置notification在前台展示
         */
        startForeground(NOTIFICATION_ID, notification);
    }
}
