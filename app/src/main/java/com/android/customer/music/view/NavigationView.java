package com.android.customer.music.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.customer.music.R;

/**
 * Description: CustomerMusic
 * Created by gy(1984629668@qq.com)
 * Created Time on 2019/10/30 15:58
 */
public class NavigationView extends LinearLayout {
    private boolean showBack;
    private boolean showRight;
    private String title;
    private int srcBack;
    private int srcRight;
    private TextView tvTitle;

    public NavigationView(Context context) {
        this(context, null);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationView);
        title = typedArray.getString(R.styleable.NavigationView_title);
        showBack = typedArray.getBoolean(R.styleable.NavigationView_showBack, true);
        showRight = typedArray.getBoolean(R.styleable.NavigationView_showRight, true);
        srcBack = typedArray.getResourceId(R.styleable.NavigationView_srcBack, 0);
        srcRight = typedArray.getResourceId(R.styleable.NavigationView_srcRight, 0);
        typedArray.recycle();
        init(context);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    private void init(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.navigation, this);
        ImageView iv_back = view.findViewById(R.id.iv_back);
        ImageView iv_right = findViewById(R.id.iv_right);
        tvTitle = findViewById(R.id.tv_title);

        if (!"".equals(title)) {
            tvTitle.setText(title);
        }

        if (!showBack) {
            iv_back.setVisibility(GONE);
        }
        if (!showRight) {
            iv_right.setVisibility(GONE);
        }

        if (showBack) {
            if (srcBack != 0) {
                iv_back.setImageResource(srcBack);
            }
            iv_back.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Activity) context).onBackPressed();
                }
            });
        }

        if (showRight) {
            if (srcRight != 0) {
                iv_right.setImageResource(srcRight);
            }
            iv_right.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rightClickListener != null) {
                        rightClickListener.clickRight(view);
                    }
                }
            });
        }
    }

    private OnRightClickListener rightClickListener;

    public void setRightClickListener(OnRightClickListener rightClickListener) {
        this.rightClickListener = rightClickListener;
    }

    public interface OnRightClickListener {
        void clickRight(View view);
    }
}
