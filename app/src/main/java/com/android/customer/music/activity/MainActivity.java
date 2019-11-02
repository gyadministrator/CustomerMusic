package com.android.customer.music.activity;

import android.util.Log;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.R;
import com.android.customer.music.adapter.MainAdapter;
import com.android.customer.music.adapter.RecyclerAdapter;
import com.android.customer.music.helper.LoadingDialogHelper;
import com.android.customer.music.model.RecommendMusicModel;
import com.android.customer.music.presenter.MainPresenter;
import com.android.customer.music.view.MainView;
import com.android.customer.music.view.RecyclerDecoration;
import com.blankj.utilcode.util.ToastUtils;

public class MainActivity extends BaseActivity implements MainView {
    private RecyclerView rv_recommend;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private static final String TAG = "MainActivity";
    private MainPresenter<MainView> mainPresenter;

    @Override
    protected void initView() {
        rv_recommend = fd(R.id.rv_recommend);
        recyclerView = fd(R.id.recyclerView);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initAction() {
        mainPresenter = new MainPresenter<MainView>(this);
        mainPresenter.getRecommendList();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        mainAdapter = new MainAdapter(this, recyclerView, mainPresenter.getTitles(), mainPresenter.getTypes());
        recyclerView.setAdapter(mainAdapter);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public void success(RecommendMusicModel result) {
        setGridData(result);
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
        ToastUtils.showShort(msg);
    }

    @Override
    public void showLoading(String msg) {
        LoadingDialogHelper.show(this, msg);
    }

    @Override
    public void dismissLoading() {
        LoadingDialogHelper.dismiss();
    }
}
