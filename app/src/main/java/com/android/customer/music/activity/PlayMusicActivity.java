package com.android.customer.music.activity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.customer.music.R;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.view.PlayMusicView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlayMusicActivity extends BaseActivity {
    private ImageView mIvBg;
    private PlayMusicView playMusicView;

    @Override
    protected void initView() {
        mIvBg = fd(R.id.iv_bg);
        playMusicView = fd(R.id.play_music_view);
    }

    @Override
    protected void initData() {
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Glide.with(this).load(Constants.DEFAULT_URL)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 10)))
                .into(mIvBg);

        playMusicView.setMusicIcon(Constants.DEFAULT_URL);
        playMusicView.playMusic();
    }

    @Override
    protected void initAction() {

    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_play_music;
    }
}
