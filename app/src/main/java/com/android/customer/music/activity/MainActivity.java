package com.android.customer.music.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.R;
import com.android.customer.music.adapter.MainAdapter;
import com.android.customer.music.adapter.RecyclerAdapter;
import com.android.customer.music.event.MusicEvent;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.MediaPlayerHelper;
import com.android.customer.music.helper.RealmHelper;
import com.android.customer.music.model.Music;
import com.android.customer.music.model.RecommendMusicModel;
import com.android.customer.music.presenter.MainPresenter;
import com.android.customer.music.utils.NotificationUtil;
import com.android.customer.music.utils.NotificationUtils;
import com.android.customer.music.view.MainView;
import com.android.customer.music.view.NavigationView;
import com.android.customer.music.view.RecyclerDecoration;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.bugly.beta.Beta;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements MainView, OnRefreshListener, MainAdapter.OnMainAdapterListener, NavigationView.OnRightClickListener, View.OnClickListener {
    private RecyclerView rv_recommend;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private MainPresenter<MainView> mainPresenter;
    private SmartRefreshLayout refreshLayout;
    private ShimmerRecyclerView mShimmerRecyclerView;
    private ShimmerRecyclerView gridShimmerRecyclerView;
    private NavigationView mNavigationView;
    private LinearLayout llBottomBar;
    private RealmHelper mRealmHelper;
    private CircleImageView ivIcon;
    private TextView tvName;
    private TextView tvAuthor;
    private ImageView ivPlay;
    private Music mMusic;
    private MediaPlayerHelper mMediaPlayerHelper;
    private Bitmap bitmap;
    private boolean isFirst = true;

    @Override
    protected void initView() {
        rv_recommend = fd(R.id.rv_recommend);
        recyclerView = fd(R.id.recyclerView);
        refreshLayout = fd(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        mShimmerRecyclerView = fd(R.id.shimmer_recycler_view);
        gridShimmerRecyclerView = fd(R.id.grid_shimmer_recycler_view);
        mNavigationView = fd(R.id.navigation);
        mNavigationView.setRightClickListener(this);
        llBottomBar = fd(R.id.ll_bottom_bar);
        ivIcon = fd(R.id.iv_icon);
        ivPlay = fd(R.id.iv_play);
        tvAuthor = fd(R.id.tv_author);
        tvName = fd(R.id.tv_name);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initData() {
        //检测通知栏权限
        checkPermission();
        //检测版本更新
        checkUpdate();
        gridShimmerRecyclerView.showShimmerAdapter();
        mMediaPlayerHelper = MediaPlayerHelper.getInstance(mActivity);
        initBottomBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initAction() {
        mainPresenter = new MainPresenter<MainView>(this);
        mainPresenter.getRecommendList();
        mShimmerRecyclerView.showShimmerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
        mainAdapter = new MainAdapter(this, recyclerView, mainPresenter.getTitles(), mainPresenter.getTypes());
        mainAdapter.setOnMainAdapterListener(this);
        recyclerView.setAdapter(mainAdapter);
    }

    private void checkUpdate() {
        Beta.checkUpgrade();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initBottomBar() {
        mRealmHelper = RealmHelper.getInstance();
        mMusic = mRealmHelper.getOne();
        if (mMusic != null) {
            final String imageUrl = mMusic.getImageUrl();
            Glide.with(mActivity).load(imageUrl).into(ivIcon);
            tvName.setText(mMusic.getTitle());
            tvAuthor.setText(mMusic.getAuthor());
            ivPlay.setOnClickListener(this);
            llBottomBar.setOnClickListener(bottomBarClickListener);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    bitmap = returnBitmap(imageUrl);
                }
            }).start();
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //启动通知栏
            //NotificationUtils.setMusic(mMusic);
            //NotificationUtils.sendCustomNotification(mActivity, mMusic, bitmap, R.mipmap.play);
        }
    }

    /**
     * 根据图片的url路径获得Bitmap对象
     * *
     *
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealmHelper.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void checkPermission() {
        NotificationUtil.checkNotificationEnable(mActivity);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void success(RecommendMusicModel result) {
        gridShimmerRecyclerView.hideShimmerAdapter();
        setGridData(result);
        refreshLayout.finishRefresh(2000);
    }

    private void setGridData(RecommendMusicModel result) {
        rv_recommend.setLayoutManager(new GridLayoutManager(this, 3));
        rv_recommend.addItemDecoration(new RecyclerDecoration(getResources().getDimensionPixelSize(R.dimen.itemWidth)));
        rv_recommend.setNestedScrollingEnabled(false);
        recyclerAdapter = new RecyclerAdapter(mActivity, result.getResult().getList());
        rv_recommend.setAdapter(recyclerAdapter);
    }

    @Override
    public void fail(String msg) {
        gridShimmerRecyclerView.hideShimmerAdapter();
        ToastUtils.showShort(msg);
        refreshLayout.finishRefresh(false);//传入false表示刷新失败
    }

    @Override
    public void showLoading(String msg) {
        LoadingDialogHelper.show(this, msg);
    }

    @Override
    public void dismissLoading() {
        LoadingDialogHelper.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        initAction();
        gridShimmerRecyclerView.showShimmerAdapter();
    }

    @Override
    public void success() {
        mShimmerRecyclerView.hideShimmerAdapter();
    }

    @Override
    public void clickRight(View view) {
        Intent intent = new Intent(mActivity, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickLeft(View view) {
        //设置界面
        startActivity(new Intent(mActivity, SettingActivity.class));
    }

    private View.OnClickListener bottomBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //底部栏点击事件
            if (mMusic != null) {
                PlayMusicActivity.startActivity(mActivity, mMusic.getImageUrl(), mMusic.getTitle(), mMusic.getAuthor(), mMusic.getSongId());
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void hasNet() {
        super.hasNet();
        initAction();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onEvent(Object object) {
        super.onEvent(object);
        if (object instanceof MusicEvent) {
            isFirst = false;
            initBottomBar();
            if (mMediaPlayerHelper.isPlaying()) {
                ivPlay.setImageResource(R.mipmap.stop);
                Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.play_music_anim);
                ivIcon.startAnimation(animation);
            } else {
                ivPlay.setImageResource(R.mipmap.play);
                ivIcon.clearAnimation();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (isFirst) {
            isFirst = false;
            if (mMusic != null) {
                PlayMusicActivity.startActivity(mActivity, mMusic.getImageUrl(), mMusic.getTitle(), mMusic.getAuthor(), mMusic.getSongId());
            }
            return;
        }
        //底部播放按钮
        if (mMediaPlayerHelper.isPlaying()) {
            ivPlay.setImageResource(R.mipmap.play);
            mMediaPlayerHelper.pause();
            ivIcon.clearAnimation();
            NotificationUtils.sendCustomNotification(mActivity, mMusic, bitmap, R.mipmap.play);
        } else {
            ivPlay.setImageResource(R.mipmap.stop);
            mMediaPlayerHelper.start();
            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.play_music_anim);
            ivIcon.startAnimation(animation);
            NotificationUtils.sendCustomNotification(mActivity, mMusic, bitmap, R.mipmap.stop);
        }
    }
}
