package com.aries.ui.util;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.aries.ui.helper.navigation.KeyboardHelper;

/**
 * Created: AriesHoo on AriesHoo on 2017/8/17 11:51
 * E-Mail: AriesHoo@126.com
 * Function: 解决底部输入框和软键盘的问题
 * Description:
 * 1、2018-2-7 10:24:38将该方法标记为废弃请使用{@link KeyboardHelper}解决
 */
@Deprecated
public class KeyboardUtil {

    private Window mWindow;
    private View mDecorView;
    private View mContentView;
    private int mKeyMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;

    public static KeyboardUtil with(Activity activity) {
        if (activity == null)
            throw new IllegalArgumentException("Activity不能为null");
        return new KeyboardUtil(activity);
    }

    private KeyboardUtil(Activity activity) {
        this(activity, ((ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0));
    }

    private KeyboardUtil(Activity activity, View contentView) {
        this(activity, null, contentView);
    }

    private KeyboardUtil(Activity activity, Dialog dialog) {
        this(activity, dialog, dialog.getWindow().findViewById(android.R.id.content));
    }

    private KeyboardUtil(Activity activity, Dialog dialog, View contentView) {
        this.mWindow = dialog != null ? dialog.getWindow() : activity.getWindow();
        this.mDecorView = activity.getWindow().getDecorView();
        this.mContentView = contentView != null ? contentView
                : mWindow.getDecorView().findViewById(android.R.id.content);
    }

    private KeyboardUtil(Activity activity, Window window) {
        this.mWindow = window;
        this.mDecorView = activity.getWindow().getDecorView();
        ViewGroup frameLayout = (ViewGroup) mWindow.getDecorView().findViewById(android.R.id.content);
        this.mContentView = frameLayout.getChildAt(0) != null ? frameLayout.getChildAt(0) : frameLayout;
    }

    /**
     * 监听layout变化
     */
    public KeyboardUtil setEnable() {
        return setEnable(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * 设置监听
     *
     * @param mode
     */
    public KeyboardUtil setEnable(int mode) {
        mWindow.setSoftInputMode(mode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 当在一个视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变时,所要调用的回调函数的接口类
            mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
        return this;
    }

    /**
     * 取消监听
     */
    public KeyboardUtil setDisable() {
        return setDisable(mKeyMode);
    }

    /**
     * 取消监听
     *
     * @param mode
     */
    public KeyboardUtil setDisable(int mode) {
        mWindow.setSoftInputMode(mode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDecorView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
        return this;
    }

    /**
     * 设置View变化监听
     */
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            mDecorView.getWindowVisibleDisplayFrame(r); //获取当前窗口可视区域大小的
            int height = mDecorView.getContext().getResources().getDisplayMetrics().heightPixels; //获取屏幕密度，不包含导航栏
            int diff = height - r.bottom;
            if (diff >= 0) {
                mContentView.setPadding(0, mContentView.getPaddingTop(), 0, diff);
            }
        }
    };
}
