package com.android.customer.music.helper;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.android.customer.music.R;
import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/11/2 16:43
 */
public class LoadingDialogHelper {
    @SuppressLint("StaticFieldLeak")
    private static KProgressHUD progressHUD;

    /**
     * 显示等待框
     *
     * @param activity activity
     */
    public static void show(Activity activity, String msg) {
        progressHUD = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(activity.getResources().getString(R.string.app_name))
                .setDetailsLabel(msg)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    /**
     * 隐藏等待框
     */
    public static void dismiss() {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
            progressHUD = null;
        }
    }
}
