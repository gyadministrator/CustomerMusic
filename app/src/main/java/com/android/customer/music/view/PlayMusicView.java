package com.android.customer.music.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.customer.music.R;
import com.android.customer.music.helper.MediaPlayerHelper;
import com.android.customer.music.model.Music;
import com.android.customer.music.model.MusicModel;
import com.android.customer.music.service.MusicService;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/1 13:03
 */
public class PlayMusicView extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private View mView;
    private boolean isPlaying, isBindService;
    private CircleImageView mIvIcon;
    private ImageView mIvNeedle, mIvPlay;
    private FrameLayout mFlPlayMusic;
    private MediaPlayerHelper mMediaPlayerHelper;
    private String mPath;
    private Intent mServiceIntent;
    private MusicService.MusicBind mMusicBind;
    private Music mMusic;
    private Animation mPlayMusicAnim, mPlayNeedleAnim, mStopNeedleAnim;

    public PlayMusicView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PlayMusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.play_music, this, false);
        mIvIcon = mView.findViewById(R.id.iv_icon);
        mFlPlayMusic = mView.findViewById(R.id.fl_play_music);
        mIvNeedle = mView.findViewById(R.id.iv_needle);
        mIvPlay = mView.findViewById(R.id.iv_play);
        mFlPlayMusic.setOnClickListener(this);
        /**
         * 定义所需要执行的动画
         * 1.光盘转动的动画
         * 2.指针指向光盘的动画
         * 3.指针离开光盘的动画
         *
         * startAnimation
         */
        mPlayMusicAnim = AnimationUtils.loadAnimation(mContext, R.anim.play_music_anim);
        mPlayNeedleAnim = AnimationUtils.loadAnimation(mContext, R.anim.play_needle_anim);
        mStopNeedleAnim = AnimationUtils.loadAnimation(mContext, R.anim.stop_needle_anim);
        addView(mView);
        mMediaPlayerHelper = MediaPlayerHelper.getInstance(mContext);
    }

    /**
     * 播放音乐
     */
    public void playMusic(String path) {
        mPath = path;
        isPlaying = true;
        mIvPlay.setVisibility(GONE);
        mFlPlayMusic.setAnimation(mPlayMusicAnim);
        mIvNeedle.setAnimation(mPlayNeedleAnim);
        /**
         * 1.判断当前音乐是否是已经在播放的音乐
         * 2.如果当前的音乐是已经在播放的音乐，那么直接执行start方法
         * 3.如果当前播放的音乐不是需要播放的音乐的话，那么就调用setPath的方法
         */
        if (mMediaPlayerHelper.getPath() != null && mMediaPlayerHelper.getPath().equals(path)) {
            mMediaPlayerHelper.start();
        } else {
            mMediaPlayerHelper.setPath(path);
            mMediaPlayerHelper.setOnMediaPlayerHelperListener(new MediaPlayerHelper.OnMediaPlayerHelperListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mMediaPlayerHelper.start();
                }

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                }
            });
        }

        startMusicService();
    }

    /**
     * 停止播放
     */
    public void stopMusic() {
        isPlaying = false;
        mIvPlay.setVisibility(VISIBLE);
        mFlPlayMusic.clearAnimation();
        mIvNeedle.setAnimation(mStopNeedleAnim);
        mMediaPlayerHelper.pause();

        if (mMusicBind != null) {
            mMusicBind.stopMusic();
        }
    }

    /**
     * 设置光盘中显示的音乐封面图片
     */
    public void setMusicIcon(String icon) {
        Glide.with(mContext)
                .load(icon)
                .into(mIvIcon);
    }

    public void setMusic(Music music) {
        mMusic = music;
    }

    @Override
    public void onClick(View view) {
        trigger();
    }

    /**
     * 切换播放状态
     */
    private void trigger() {
        if (isPlaying) {
            stopMusic();
        } else {
            playMusic(mPath);
        }
    }

    /**
     * 启动音乐服务
     */
    private void startMusicService() {
        if (mServiceIntent == null) {
            mServiceIntent = new Intent(mContext, MusicService.class);
            mContext.startService(mServiceIntent);
        } else {
            mMusicBind.playMusic();
        }
        //绑定service,当前service未绑定，绑定服务
        if (!isBindService) {
            isBindService = true;
            mContext.bindService(mServiceIntent, conn, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * 解除绑定
     */
    public void destory() {
        if (isBindService) {
            isBindService = false;
            mContext.unbindService(conn);
        }
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMusicBind = (MusicService.MusicBind) iBinder;
            mMusicBind.setMusic(mMusic);
            mMusicBind.playMusic();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
}
