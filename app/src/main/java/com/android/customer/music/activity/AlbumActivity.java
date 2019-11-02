package com.android.customer.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.R;
import com.android.customer.music.adapter.LinearAdapter;
import com.android.customer.music.adapter.MainAdapter;
import com.android.customer.music.constant.Constants;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.helper.RetrofitHelper;
import com.android.customer.music.model.MusicModel;
import com.android.customer.music.view.NavigationView;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AlbumActivity extends BaseActivity {
    private NavigationView navigation;
    private TextView tvTopTitle;
    private RecyclerView rv_linear;
    private LinearAdapter linerAdapter;
    private int mType;

    @Override
    protected void initView() {
        navigation = fd(R.id.navigation);
        tvTopTitle = fd(R.id.tv_top_title);
        rv_linear = fd(R.id.rv_linear);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String mTitle = intent.getStringExtra("title");
        mType = intent.getIntExtra("type", 1);
        tvTopTitle.setText(mTitle);
        navigation.setTitle(mTitle);
    }

    @Override
    protected void initAction() {
        setData();
    }

    private void setData() {
        LoadingDialogHelper.show(mActivity, "加载中...");
        RetrofitHelper retrofitHelper = RetrofitHelper.getInstance();
        Map<String, Object> params = retrofitHelper.getmParams();
        params.put("method", Constants.METHOD_LIST);
        params.put("type", mType);
        params.put("size", 20);
        params.put("offset", 0);
        Observable<MusicModel> observable = retrofitHelper.initRetrofit().list(params);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MusicModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MusicModel musicModel) {
                        rv_linear.setLayoutManager(new LinearLayoutManager(mActivity));
                        rv_linear.addItemDecoration(new DividerItemDecoration(mActivity, RecyclerView.VERTICAL));
                        linerAdapter = new LinearAdapter(mActivity, rv_linear);
                        linerAdapter.setList(musicModel.getSong_list(), false);
                        rv_linear.setAdapter(linerAdapter);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort(Objects.requireNonNull(e.getMessage()));
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
     * @param context context
     * @param title   标题
     * @param type    类型
     */
    public static void startActivity(Context context, String title, int type) {
        Intent intent = new Intent(context, AlbumActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_album;
    }
}
