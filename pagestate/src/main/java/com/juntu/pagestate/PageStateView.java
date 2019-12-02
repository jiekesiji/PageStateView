package com.juntu.pagestate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Created by cj on 2019/11/29.
 * Email:codesexy@163.com
 * Function:一个页面状态管理类
 * desc:
 */
public class PageStateView extends FrameLayout {

    private static final String TAG = "PageStateView";

    enum State {
        EMPTY,//数据为空
        LOADING,//正在加载
        ERROR,//加载错误
        CONTENT,//有内容
        NONETWORK,//无网络
        CUSTOM//自定义
    }

    private View emptyView;
    private View loadingView;
    private View errorView;
    private View contentView;
    private View noNetView;
    private View customView;
    private LoadingView loading;
    private boolean isFirst = true;

    private PageStateView(Context context) {
        this(context, null);
    }

    private PageStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private PageStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    /**
     * 显示原本内容
     */
    public void content() {
        showView(State.CONTENT);
    }

    /**
     * 显示错误
     */
    public void error() {
        showView(State.ERROR);
    }

    /**
     * 显示无网络
     */
    public void noNet() {
        showView(State.NONETWORK);
    }

    /**
     * 显示加载动画
     */
    public void loading() {
        showView(State.LOADING);
    }


    /**
     * 显示自定义布局
     */
    public void custom() {
        showView(State.CUSTOM);
    }


    /**
     * 显示无数据的布局
     */
    public void noData() {
        showView(State.EMPTY);
    }


    private void showView(final State state) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            changeView(state);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    changeView(state);
                }
            });
        }
    }

    /**
     * @param state
     */
    private void changeView(State state) {

        if (state == State.LOADING) {
            if (loading != null) loading.startRunning();
        } else if (loading != null) loading.stopRunning();

        if (errorView != null)
            errorView.setVisibility(state == State.ERROR ? VISIBLE : GONE);

        if (emptyView != null)
            emptyView.setVisibility(state == State.EMPTY ? VISIBLE : GONE);

        if (loadingView != null)
            loadingView.setVisibility(state == State.LOADING ? VISIBLE : GONE);

        if (contentView != null)
            contentView.setVisibility(state == State.CONTENT ? VISIBLE : GONE);

        if (noNetView != null)
            noNetView.setVisibility(state == State.NONETWORK ? VISIBLE : GONE);

        if (customView != null)
            customView.setVisibility(state == State.CUSTOM ? VISIBLE : GONE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (isFirst) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                layoutParams.width = right - left;
                layoutParams.height = bottom - top;
                child.setLayoutParams(layoutParams);
            }
            isFirst = false;
        }

    }


    public static class Builder {

        private PageStateView stateView;
        private LayoutInflater inflater;
        private Context context;
        private OnRetryClickListener listener;

        public Builder(@NonNull Context context) {
            this.context = context;
            stateView = new PageStateView(context);
            inflater = LayoutInflater.from(context);

        }

        private void initDefault() {
            initLayout(State.ERROR);
            initLayout(State.LOADING);
            initLayout(State.EMPTY);
            initLayout(State.NONETWORK);
        }

        private void initLayout(State state) {
            int layoutId = 0;
            View view = null;
            if (state == State.ERROR) {
                layoutId = R.layout.layout_page_state_error;
            } else if (state == State.LOADING) {
                layoutId = R.layout.layout_page_state_loading;
            } else if (state == State.EMPTY) {
                layoutId = R.layout.layout_page_state_empty;
            } else if (state == State.NONETWORK) {
                layoutId = R.layout.layout_page_state_nonet;
            }

            view = inflater.inflate(layoutId, null, false);
            view.setBackgroundColor(Color.WHITE);
            setDefaultViewListener(state, view);
            stateView.addView(view);
        }

        private void setDefaultViewListener(State state, View view) {
            if (state == State.ERROR) {
                stateView.errorView = view;
                stateView.errorView.setVisibility(GONE);
                setOnclickListener(view.findViewById(R.id.iv_error));
                setOnclickListener(view.findViewById(R.id.tv_error));
            } else if (state == State.LOADING) {
                stateView.loadingView = view;
                stateView.loading = view.findViewById(R.id.loadView);
            } else if (state == State.EMPTY) {
                stateView.emptyView = view;
                stateView.emptyView.setVisibility(GONE);
                setOnclickListener(view.findViewById(R.id.iv_no_data));
                setOnclickListener(view.findViewById(R.id.tv_no_data));
            } else if (state == State.NONETWORK) {
                stateView.noNetView = view;
                stateView.noNetView.setVisibility(GONE);
                setOnclickListener(view.findViewById(R.id.iv_no_net));
                setOnclickListener(view.findViewById(R.id.tv_no_net));
            }
        }


        private void setOnclickListener(View view) {
            if (view != null)
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) listener.onRetry();
                    }
                });
        }

        public Builder init(Object obj) {
            if (obj == null) {
                throw new IllegalArgumentException("obj must not be null");
            }

            ViewGroup parentView = null;
            if (obj instanceof View) {
                View target = (View) obj;
                try {
                    parentView = (ViewGroup) target.getParent();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (obj instanceof Activity) {
                Activity activity = (Activity) obj;
                parentView = activity.findViewById(android.R.id.content);
            } else if (obj instanceof Fragment) {
                Fragment fragment = (Fragment) obj;
                parentView = (ViewGroup) fragment.getView();
            } else if (obj instanceof android.app.Fragment) {
                android.app.Fragment fragment = (android.app.Fragment) obj;
                parentView = (ViewGroup) fragment.getView();
            }

            if (parentView == null) throw new IllegalArgumentException("obj must  be one of view activity fragment");

            View targetContent = null;
            int index = 0;//对于是view这中情况  找出角标
            //需要替换的是view的需要特别处理
            if (obj instanceof View) {
                targetContent = (View) obj;
                for (int i = 0; i < parentView.getChildCount(); i++) {
                    if (parentView.getChildAt(i) == obj) index = i;
                }
            } else {
                targetContent = parentView.getChildAt(0);
            }

            stateView.contentView = targetContent;

            stateView.removeAllViews();
            //移除 之前的view   添加现在的管理器  这个地方造成targetContent的宽高信息丢失
            //所以在最后摆放的时候需要重新设置targetContent 的宽高
            parentView.removeView(targetContent);
            parentView.addView(stateView, index, targetContent.getLayoutParams());
            stateView.addView(targetContent);
            initDefault();
            return this;
        }


        public Builder setLoadingView(View view) {
            if (view == null) return this;
            checkState();
            addStateView(view, State.LOADING);
            return this;
        }


        public Builder setErrorView(View view) {
            if (view == null) return this;
            checkState();
            addStateView(view, State.ERROR);
            return this;
        }


        public Builder setEmptyView(View view) {
            if (view == null) return this;
            checkState();
            addStateView(view, State.EMPTY);
            return this;
        }

        public Builder setNoNetView(View view) {
            if (view == null) return this;
            checkState();
            addStateView(view, State.NONETWORK);
            return this;
        }

        /**
         * @param view
         */
        public Builder setCustomView(View view) {
            if (view == null) return this;
            checkState();
            addStateView(view, State.CUSTOM);
            return this;
        }

        private void checkState() {
            if (stateView.getChildCount() == 0) {
                throw new IllegalStateException("you should call Build$init method at first");
            }
        }

        private void addStateView(View view, State state) {
            if (state == State.ERROR) {
                if (stateView.errorView != null) stateView.removeView(stateView.errorView);
                stateView.errorView = view;
            }

            if (state == State.EMPTY) {
                if (stateView.emptyView != null) stateView.removeView(stateView.emptyView);
                stateView.emptyView = view;
            }

            if (state == State.LOADING) {
                if (stateView.loadingView != null) stateView.removeView(stateView.loadingView);
                stateView.loadingView = view;
            }


            if (state == State.NONETWORK) {
                if (stateView.noNetView != null) stateView.removeView(stateView.noNetView);
                stateView.noNetView = view;
            }

            if (state == State.CUSTOM) {
                if (stateView.customView != null) stateView.removeView(stateView.customView);
                stateView.customView = view;
            }

            stateView.addView(view);
            if (state != State.LOADING)
                view.setVisibility(GONE);
        }

        public PageStateView build() {
            return stateView;
        }

        public Builder setRetryListener(OnRetryClickListener listener) {
            this.listener = listener;
            return this;
        }

        public interface OnRetryClickListener {
            void onRetry();
        }
    }


}
