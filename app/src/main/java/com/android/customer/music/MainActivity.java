package com.android.customer.music;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.activity.BaseActivity;
import com.android.customer.music.adapter.LinearAdapter;
import com.android.customer.music.adapter.RecyclerAdapter;
import com.android.customer.music.view.RecyclerDecoration;

public class MainActivity extends BaseActivity {
    private RecyclerView rv_recommend;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView rv_new;
    private LinearAdapter linerAdapter;

    @Override
    protected void initView() {
        rv_recommend = fd(R.id.rv_recommend);
        rv_new = fd(R.id.rv_new);
    }

    @Override
    protected void initData() {
        rv_recommend.setLayoutManager(new GridLayoutManager(this, 3));
        rv_recommend.addItemDecoration(new RecyclerDecoration(getResources().getDimensionPixelSize(R.dimen.itemWidth)));
        rv_recommend.setNestedScrollingEnabled(false);
        recyclerAdapter = new RecyclerAdapter(mActivity);
        rv_recommend.setAdapter(recyclerAdapter);

        rv_new.setLayoutManager(new LinearLayoutManager(this));
        rv_new.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        rv_new.setNestedScrollingEnabled(false);
        linerAdapter = new LinearAdapter(mActivity, rv_new);
        rv_new.setAdapter(linerAdapter);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
}
