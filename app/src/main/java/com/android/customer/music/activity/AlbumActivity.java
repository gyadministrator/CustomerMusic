package com.android.customer.music.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.customer.music.R;
import com.android.customer.music.adapter.LinearAdapter;
import com.android.customer.music.view.NavigationView;

public class AlbumActivity extends BaseActivity {
    private NavigationView navigation;
    private TextView tvTopTitle;
    private RecyclerView rv_linear;
    private LinearAdapter linerAdapter;
    private String mTitle;
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
        mTitle = intent.getStringExtra("title");
        mType = intent.getIntExtra("type", 1);
        rv_linear.setLayoutManager(new LinearLayoutManager(this));
        rv_linear.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        rv_linear.setNestedScrollingEnabled(false);
        linerAdapter = new LinearAdapter(mActivity, rv_linear);
        rv_linear.setAdapter(linerAdapter);
    }

    @Override
    protected void initAction() {

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
