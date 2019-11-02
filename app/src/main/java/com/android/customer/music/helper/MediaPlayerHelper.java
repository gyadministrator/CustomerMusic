package com.android.customer.music.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/1 23:18
 */
public class MediaPlayerHelper {
    private static MediaPlayerHelper instance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mPath;
    private OnMediaPlayerHelperListener onMediaPlayerHelperListener;

    public void setOnMediaPlayerHelperListener(OnMediaPlayerHelperListener onMediaPlayerHelperListener) {
        this.onMediaPlayerHelperListener = onMediaPlayerHelperListener;
    }

    public static MediaPlayerHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (MediaPlayerHelper.class) {
                if (instance == null) {
                    instance = new MediaPlayerHelper(context);
                }
            }
        }
        return instance;
    }

    private MediaPlayerHelper(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
    }

    /**
     * 1.setPath 当前需要播放的音乐
     * 2.start 播放音乐
     * 3.pause 暂停播放
     */

    public void setPath(String path) {
        /**
         * 1.音乐正在播放，重置音乐播放状态
         * 2.设置播放音乐路径
         * 3.准备播放
         */
        mPath = path;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (onMediaPlayerHelperListener != null) {
                    onMediaPlayerHelperListener.onPrepared(mediaPlayer);
                }
            }
        });
    }

    /**
     * 返回正在播放的音乐路径
     *
     * @return
     */
    public String getPath() {
        return mPath;
    }

    /**
     * 播放音乐
     */
    public void start() {
        if (mMediaPlayer.isPlaying()) return;
        mMediaPlayer.start();
    }

    /**
     * 暂停音乐
     */
    public void pause() {
        mMediaPlayer.pause();
    }

    public interface OnMediaPlayerHelperListener {
        void onPrepared(MediaPlayer mediaPlayer);
    }

}