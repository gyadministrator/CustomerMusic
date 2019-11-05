package com.android.customer.music.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.android.customer.music.BuildConfig;
import com.android.customer.music.R;
import com.android.customer.music.utils.DataCleanManager;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.ToastUtils;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvVersion;
    private TextView mTvClean;

    @Override
    protected void initView() {
        mTvClean = fd(R.id.tv_clean);
        mTvVersion = fd(R.id.tv_version);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        mTvVersion.setText("当前版本\t\tv" + BuildConfig.VERSION_NAME);
        mTvClean.setOnClickListener(this);
        try {
            String totalCacheSize = DataCleanManager.getTotalCacheSize(mActivity);
            mTvClean.setText("清除缓存\t\t" + totalCacheSize);
        } catch (Exception e) {
            e.printStackTrace();
            mTvClean.setText("清除缓存\t\t0M");
        }
    }

    @Override
    protected void initAction() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        CleanUtils.cleanExternalCache();
        CleanUtils.cleanInternalFiles();
        CleanUtils.cleanInternalSp();
        mTvClean.setText("清除缓存\t\t0M");
        ToastUtils.showShort("清除成功");
    }
}
