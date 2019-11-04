package com.android.customer.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.customer.music.R;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.event.MusicEvent;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RealmHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.Music;
import com.android.customer.music.model.PlayMusicModel;
import com.android.customer.music.view.PlayMusicView;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class PlayMusicActivity extends BaseActivity {
    private ImageView mIvBg;
    private PlayMusicView playMusicView;
    private String songId;
    private TextView tvName;
    private TextView tvAuthor;
    private Music mMusic;
    private RealmHelper mRealmHelper;

    @Override
    protected void initView() {
        mIvBg = fd(R.id.iv_bg);
        playMusicView = fd(R.id.play_music_view);
        tvName = fd(R.id.tv_name);
        tvAuthor = fd(R.id.tv_author);
    }

    @Override
    protected void initData() {
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String imageUrl = intent.getStringExtra("imageUrl");
        String author = intent.getStringExtra("author");
        songId = intent.getStringExtra("songId");
        Glide.with(this).load(imageUrl)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 10)))
                .into(mIvBg);
        tvAuthor.setText(author);
        tvName.setText(name);
        playMusicView.setMusicIcon(imageUrl);
        mMusic = new Music();
        mMusic.setAuthor(author);
        mMusic.setTitle(name);
        mMusic.setImageUrl(imageUrl);
        mMusic.setSongId(songId);
        mRealmHelper = RealmHelper.getInstance();
    }

    @Override
    protected void initAction() {
        playMusic(songId);
    }

    private void playMusic(final String songId) {
        LoadingDialogHelper.show(mActivity, "加载中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_PLAY);
        params.put("songid", songId);
        Observable<PlayMusicModel> observable = retrofitHelper.initRetrofit().play(params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PlayMusicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PlayMusicModel playMusicModel) {
                        if (!TextUtils.isEmpty(playMusicModel.getBitrate().getFile_link())) {
                            mMusic.setPath(playMusicModel.getBitrate().getFile_link());
                            playMusicView.setMusic(mMusic);
                            playMusicView.playMusic(playMusicModel.getBitrate().getFile_link());
                            //保存到数据库
                            mRealmHelper.deleteAll();
                            mRealmHelper.save(mMusic);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort("该歌曲暂时无法获取播放源");
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
    }

    /**
     * 启动活动
     *
     * @param context  context
     * @param imageUrl 图片地址
     * @param name     标题
     * @param author   作者
     * @param songId   songId
     */
    public static void startActivity(Context context, String imageUrl, String name, String author, String songId) {
        Intent intent = new Intent(context, PlayMusicActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        intent.putExtra("name", name);
        intent.putExtra("author", author);
        intent.putExtra("songId", songId);
        context.startActivity(intent);
    }

    public void onBackClick(View view) {
        onBackPressed();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_play_music;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playMusicView.close();
    }
}
