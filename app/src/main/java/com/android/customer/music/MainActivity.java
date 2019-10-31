package com.android.customer.music;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.activity.BaseActivity;
import com.android.customer.music.adapter.RecyclerAdapter;
import com.android.customer.music.view.RecyclerDecoration;

public class MainActivity extends BaseActivity {
    private RecyclerView rv;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void initView() {
        rv = fd(R.id.rv);
    }

    @Override
    protected void initData() {
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.addItemDecoration(new RecyclerDecoration(getResources().getDimensionPixelSize(R.dimen.itemWidth)));
        recyclerAdapter = new RecyclerAdapter(mActivity);
        rv.setAdapter(recyclerAdapter);
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
}
