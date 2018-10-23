package org.zcx.replaceviewlibrary;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class ReplaceView extends FrameLayout {
    private static final int CONTENT_VIEW_TAG = 222;
    private View contentView;

    public SparseArray<View> replaceViews;
    private SparseArray<OnClickListener> clickListeners;
    private int useing;


    private ReplaceView(@NonNull Context context) {
        this(context, null);
    }

    private ReplaceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private ReplaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setForegroundGravity(Gravity.CENTER);
        setBackgroundColor(0x0f0);
        replaceViews = new SparseArray<>();
        clickListeners = new SparseArray<>();
    }

    @RequiresApi(api = android.os.Build.VERSION_CODES.LOLLIPOP)
    private ReplaceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public View getContentView() {
        return contentView;
    }

    private void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public SparseArray<View> getReplaceViews() {
        return replaceViews;
    }

    private void setReplaceViews(SparseArray<View> replaceViews) {
        this.replaceViews = replaceViews;
    }

    public SparseArray<OnClickListener> getClickListeners() {
        return clickListeners;
    }

    public void setClickListeners(SparseArray<OnClickListener> clickListeners) {
        this.clickListeners = clickListeners;
    }

    public boolean isUseing(int tag) {
        return tag == useing;
    }

    public void showReplaceView(int tag) {
        removeAllViews();
        View view = getReplaceViews().get(tag);
        if (null == view) {
            throw new RuntimeException("没有定义为该tag的view");
        }
        useing = tag;
        addView(view);
    }

    public void hide() {
        removeAllViews();
        addView(contentView);
    }

    public void remove() {
        ViewParent parent = getParent();
        ((ViewGroup) parent).removeView(this);
        ((ViewGroup) parent).addView(contentView);
        contentView = null;
        if (null != replaceViews) {
            replaceViews.clear();
            replaceViews = null;
        }
        if (null != clickListeners) {
            clickListeners.clear();
            clickListeners = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        remove();
    }

    public static class Build {
        private View contentView;//内容的view
        private SparseArray<View> replaceViews = new SparseArray<>();
        private SparseArray<OnClickListener> clickListeners = new SparseArray<>();

        public Build() {
        }

        public Build(Activity activity) {
            contentView = activity.findViewById(android.R.id.content);
        }

        public Build(Fragment fragment) {
            contentView = fragment.getView();
        }

        public Build(View view) {
            contentView = view;
        }

        public Build setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Build addReplaceView(View view, int tag) {
            replaceViews.put(tag, view);
            return this;
        }

        public Build setClickListener(int tag, OnClickListener listener) {
            clickListeners.put(tag, listener);
            return this;
        }

        public ReplaceView create() {
            if (null == contentView) {
                throw new RuntimeException("请在Build中setContentView");
            }
            ViewParent viewParent = contentView.getParent();
            if (!(viewParent instanceof ViewGroup)) {
                throw new RuntimeException("contentView的parentView不是ViewGroup");
            }
            ViewGroup parentViewGroup = (ViewGroup) viewParent;
            ReplaceView replaceView = new ReplaceView(contentView.getContext());
            replaceView.setContentView(contentView);

            putAll(replaceView.getReplaceViews(), replaceViews);
            replaceViews.clear();
            replaceViews = null;

            putAll(replaceView.getClickListeners(), clickListeners);
            clickListeners.clear();
            clickListeners = null;

            parentViewGroup.removeView(contentView);
            parentViewGroup.addView(replaceView);
            return replaceView;
        }

        private <V> void putAll(SparseArray<V> putArray, SparseArray<V> originalArray) {
            for (int i = 0; i < originalArray.size(); i++) {
                putArray.put(originalArray.keyAt(i), originalArray.valueAt(i));
            }
        }
    }

    private interface OnClickListener {
        void onClick(View view);
    }
}
