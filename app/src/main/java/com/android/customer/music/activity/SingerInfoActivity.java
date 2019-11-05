package com.android.customer.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.android.customer.music.R;
import com.android.customer.music.api.Api;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.SingerInfoModel;
import com.android.customer.music.view.NavigationView;
import com.blankj.utilcode.util.ToastUtils;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebChromeClient;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SingerInfoActivity extends BaseActivity {
    private LinearLayout llContent;
    private String tingUid;
    private AgentWeb mAgentWeb;
    private NavigationView navigationView;

    @Override
    protected void initView() {
        llContent = fd(R.id.ll_content);
        navigationView = findViewById(R.id.navigation);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        tingUid = intent.getStringExtra("tingUid");
    }

    @Override
    protected void initAction() {
        //获取歌手详情
        getSingerInfo();
    }

    @Override
    protected void hasNet() {
        super.hasNet();
        initAction();
    }

    private void getSingerInfo() {
        LoadingDialogHelper.show(mActivity, "加载中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Map<String, Object> params = retrofitHelper.getmParams();
        Api api = retrofitHelper.initRetrofit();
        params.put("method", Constants.METHOD_SINGER);
        params.put("tinguid", tingUid);
        Observable<SingerInfoModel> observable = api.getInfo(params);
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SingerInfoModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SingerInfoModel singerInfoModel) {
                        if (singerInfoModel != null) {
                            if (singerInfoModel.getUrl() != null) {
                                setContent(singerInfoModel.getUrl());
                            }
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        LoadingDialogHelper.dismiss();
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
                    }

                    @Override
                    public void onComplete() {
                        LoadingDialogHelper.dismiss();
                    }
                });
    }

    private void setContent(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAgentWeb = AgentWeb.with(mActivity)
                        .setAgentWebParent(llContent, new LinearLayout.LayoutParams(-1, -1))
                        .useDefaultIndicator()
                        .createAgentWeb()
                        .ready()
                        .go(url);

                //获取网页的标题
                mAgentWeb.getWebCreator().getWebView().setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        if (!TextUtils.isEmpty(title)) {
                            navigationView.setTitle(title);
                        }
                        super.onReceivedTitle(view, title);
                    }
                });
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_singer_info;
    }

    /**
     * 启动活动
     *
     * @param context context
     * @param tingUid tingUid
     */
    public static void startActivity(Context context, String tingUid) {
        Intent intent = new Intent(context, SingerInfoActivity.class);
        intent.putExtra("tingUid", tingUid);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.clearWebCache();
            mAgentWeb.destroy();
        }
    }
}
