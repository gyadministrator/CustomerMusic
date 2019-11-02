package com.android.customer.music.activity;

import androidx.annotation.NonNull;
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
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class MainActivity extends BaseActivity implements MainView, OnRefreshListener, MainAdapter.OnMainAdapterListener {
    private RecyclerView rv_recommend;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private MainAdapter mainAdapter;
    private MainPresenter<MainView> mainPresenter;
    private SmartRefreshLayout refreshLayout;
    private ShimmerRecyclerView mShimmerRecyclerView;
    private ShimmerRecyclerView gridShimmerRecyclerView;

    @Override
    protected void initView() {
        rv_recommend = fd(R.id.rv_recommend);
        recyclerView = fd(R.id.recyclerView);
        refreshLayout = fd(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        mShimmerRecyclerView = fd(R.id.shimmer_recycler_view);
        gridShimmerRecyclerView = fd(R.id.grid_shimmer_recycler_view);
    }

    @Override
    protected void initData() {
        gridShimmerRecyclerView.showShimmerAdapter();
    }

    @Override
    protected void initAction() {
        mainPresenter = new MainPresenter<MainView>(this);
        mainPresenter.getRecommendList();
        mShimmerRecyclerView.showShimmerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        mainAdapter = new MainAdapter(this, recyclerView, mainPresenter.getTitles(), mainPresenter.getTypes());
        mainAdapter.setOnMainAdapterListener(this);
        recyclerView.setAdapter(mainAdapter);
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

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        initAction();
        gridShimmerRecyclerView.showShimmerAdapter();
    }

    @Override
    public void success() {
        mShimmerRecyclerView.hideShimmerAdapter();
    }
}
