package com.android.customer.music.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.customer.music.R;
import com.blankj.utilcode.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/30 14:21
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Activity mActivity;
    private NetWorkCastReceiver receiver;
    private long start;

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化操作
     */
    protected abstract void initAction();

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getContentView();

    /**
     * 获取控件的值
     *
     * @param id  id
     * @param <T> 控件
     * @return
     */
    protected <T extends View> T fd(@IdRes int id) {
        return findViewById(id);
    }

    //处理eventBus事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Object object) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if (!this.getClass().getSimpleName().contains("LauncherActivity")) {
            setTheme(R.style.AppTheme);
        }
        if (getContentView() != 0) {
            setContentView(getContentView());
        }
        //初始化控件
        initView();
        //初始化数据
        initData();
        //初始化操作
        initAction();

        //注册网络广播
        receiver = new NetWorkCastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        //注册eventBus
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!this.getClass().getSimpleName().contains("MainActivity")) {
                onBackPressed();
                return true;
            }
            if (System.currentTimeMillis() - start > 2000) {
                ToastUtils.showShort("再次点击返回到桌面");
                start = System.currentTimeMillis();
            } else {
                //回到桌面
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        }
        //解绑eventBus
        EventBus.getDefault().unregister(this);
    }

    private class NetWorkCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info != null) {
                    if (NetworkInfo.State.CONNECTED != info.getState()) {
                        //连接状态 处理自己的业务逻辑
                        Toast.makeText(context, "网络链接失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
