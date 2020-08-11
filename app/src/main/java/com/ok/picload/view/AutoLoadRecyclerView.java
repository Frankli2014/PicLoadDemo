package com.ok.picload.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ok.picload.utlis.ImageLoader;

/**
 * Created by frankli on 2016/7/18.
 */
public class AutoLoadRecyclerView extends RecyclerView {

    private onLoadMoreListener loadMoreListener;    //加载更多回调
    private boolean isLoadingMore;                  //是否加载更多
    /**
     * 默认是可以加载更多的
     */
    private boolean isCanLoadmore = true;

    public AutoLoadRecyclerView(Context context) {
        this(context, null);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        isLoadingMore = false;  //默认无需加载更多
        setOnScrollListener(new AutoLoadScrollListener(null, true, true));
    }

    /**
     * 配置显示图片，需要设置这几个参数，快速滑动时，暂停图片加载
     *
     * @param imageLoader   ImageLoader实例对象
     * @param pauseOnScroll
     * @param pauseOnFling
     */
    public void setOnPauseListenerParams(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {

        setOnScrollListener(new AutoLoadScrollListener(imageLoader, pauseOnScroll, pauseOnFling));

    }

    public void setLoadMoreListener(onLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


    public void loadFinish(Object obj) {
        isLoadingMore = false;
    }

    public void setLoadMoreEnble(boolean enble) {
        isCanLoadmore = enble;
    }


    //加载更多的回调接口
    public interface onLoadMoreListener {
        void loadMore();
    }


    /**
     * 滑动自动加载监听器
     */
    private class AutoLoadScrollListener extends OnScrollListener {

        private ImageLoader imageLoader;
        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;
        int lastVisibleItem;

        public AutoLoadScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
            this.imageLoader = imageLoader;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //由于GridLayoutManager是LinearLayoutManager子类，所以也适用
            if (getLayoutManager() instanceof LinearLayoutManager) {
                lastVisibleItem = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
//                int totalItemCount = AutoLoadRecyclerView.this.getAdapter().getItemCount();
//
//                //有回调接口，且不是加载状态，且计算后剩下2个item，且处于向下滑动，则自动加载
//                if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount -
//                        2 && dy > 0) {
//                    loadMoreListener.loadMore();
//                    isLoadingMore = true;
//                }
            }
        }

        //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；由于用户的操作，屏幕产生惯性滑动时为2
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItem + 1 == AutoLoadRecyclerView.this.getAdapter().getItemCount() && loadMoreListener != null && !isLoadingMore && isCanLoadmore) {
                loadMoreListener.loadMore();
                isLoadingMore = true;
            }

            //根据newState状态做处理
//            if (imageLoader != null) {
//                switch (newState) {
//                    case 0:
//                        imageLoader.resume();
//                        break;
//
//                    case 1:
//                        if (pauseOnScroll) {
//                            imageLoader.pause();
//                        } else {
//                            imageLoader.resume();
//                        }
//                        break;
//
//                    case 2:
//                        if (pauseOnFling) {
//                            imageLoader.pause();
//                        } else {
//                            imageLoader.resume();
//                        }
//                        break;
//                }
//            }
        }
    }

}
