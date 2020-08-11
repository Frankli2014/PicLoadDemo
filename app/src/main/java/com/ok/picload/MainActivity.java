package com.ok.picload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ok.picload.adpter.RecyclerPostAdapter;
import com.ok.picload.bean.ResponseFoo;
import com.ok.picload.contract.ClubShowContract;
import com.ok.picload.presenter.ImgListPresenter;
import com.ok.picload.view.AutoLoadRecyclerView;
import com.ok.picload.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ClubShowContract.ViewInter {


    private String TAG = "MainActivity";

    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce = false;

    private SwipeRefreshLayout mSwipeRefrssh;

    private AutoLoadRecyclerView mRecyclerView;

    private Activity mActivity;

    EditText etEnrolSearch;
    TextView tvSearchBtn;

    /***
     * 图片列表适配器
     */
    private RecyclerPostAdapter myRecyclerViewAdapter;
    /***
     * 图片列表的数据
     */
    private List<ResponseFoo.ListBean> imgList;

    /***
     * 当前请求的页数
     */
    private int pageIndex = 1;
    private int pageSize = 20;
    /**
     * 总共的页数
     */
    private int pageCount;
    /**
     * 当前是正上拉加载状态，为true时可以上拉，false时表示正在加载中
     */
    private boolean isLoading = true;


    private ImgListPresenter presenter;


    //搜索内容 动态搜素
    private String queryStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        presenter = new ImgListPresenter(this);
        initView();

    }


    @SuppressLint("WrongConstant")
    private void initView() {
        mSwipeRefrssh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (AutoLoadRecyclerView) findViewById(R.id.recycler_view);
        etEnrolSearch = findViewById(R.id.et_enrol_search);
        tvSearchBtn = findViewById(R.id.tv_search_btn);

        mSwipeRefrssh.setColorSchemeResources(R.color.green, R.color.yellow, R.color.red);
        mSwipeRefrssh.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefrssh.setOnRefreshListener(this);
        mRecyclerView.setHasFixedSize(true);


        if (imgList == null) {
            imgList = new ArrayList<>();
        }

        //布局管理器对象 参数1.上下文 2.规定一行显示几列的参数常量
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        //设置RecycleView显示的方向是水平还是垂直 GridLayout.HORIZONTAL水平  GridLayout.VERTICAL默认垂直
        gridLayoutManager.setOrientation(GridLayout.VERTICAL);
        //设置布局管理器， 参数gridLayoutManager对象
        mRecyclerView.setLayoutManager(gridLayoutManager);


        HashMap<String, Integer> spacesVelue = new HashMap<>();
        spacesVelue.put(SpacesItemDecoration.TOP_SPACE, 10);
        spacesVelue.put(SpacesItemDecoration.BOTTOM_SPACE, 20);
        spacesVelue.put(SpacesItemDecoration.LEFT_SPACE, 0);
        spacesVelue.put(SpacesItemDecoration.RIGHT_SPACE, 0);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3,spacesVelue, true));

        myRecyclerViewAdapter = new RecyclerPostAdapter(this, imgList);
        mRecyclerView.setAdapter(myRecyclerViewAdapter);

        mRecyclerView.setLoadMoreListener(new AutoLoadRecyclerView.onLoadMoreListener() {
            @Override
            public void loadMore() {
                loadMoredata();
            }
        });

        etEnrolSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showKeyboard();
                }
            }
        });

//        presenter.getimglist(queryStr, pageIndex + "", pageSize + "");

        //搜索事件
        tvSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryStr = etEnrolSearch.getText().toString().trim();
                if (TextUtils.isEmpty(queryStr)){

                    Toast.makeText(mActivity,"请输入搜索内容",Toast.LENGTH_SHORT).show();
                }
                pageIndex=1;
                presenter.getimglist(queryStr, pageIndex + "", pageSize + "");
            }
        });
    }

    /**
     * 加载更多数据
     */
    private void loadMoredata() {
        if (isLoading && pageIndex < pageCount) {
            pageIndex++;
            presenter.getimglist(queryStr, pageIndex + "", pageSize + "");
            isLoading = false;
        }
    }


    @Override
    public void onRefresh() {
        pageIndex = 1;
        presenter.getimglist(queryStr, pageIndex + "", pageSize + "");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDetach();
        }
    }

    @Override
    public void loadOver(ResponseFoo responseFoo) {


        if (responseFoo != null && responseFoo.getList().size() > 0) {
            //设置最后一条数据的时间戳
//            int sizes = responseFoo.getList().size();
            if (pageIndex == 1) {
                if (imgList != null) {
                    imgList.clear();
                }
                imgList = responseFoo.getList();

            } else {
                imgList.addAll(responseFoo.getList());
            }
            if (responseFoo.getTotal() > 0) {
                pageCount = responseFoo.getTotal() / pageSize;
                if (0 == pageCount || pageIndex == pageCount || pageCount <= 1) {
                    myRecyclerViewAdapter.setFooterStyle(false);
                    mRecyclerView.setLoadMoreEnble(false);
                } else {
                    myRecyclerViewAdapter.setFooterStyle(true);
                    mRecyclerView.setLoadMoreEnble(true);
                    myRecyclerViewAdapter.setShowFooter(true);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    myRecyclerViewAdapter.setListDatas(imgList);
                    myRecyclerViewAdapter.notifyDataSetChanged();
                }
            });


            mHasLoadedOnce = true;
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mRecyclerView.loadFinish(false);
                    myRecyclerViewAdapter.setShowFooter(false);
                    myRecyclerViewAdapter.notifyDataSetChanged();
                }
            });

        }

        isLoading = true;
        mSwipeRefrssh.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefrssh.setRefreshing(false);
                mRecyclerView.loadFinish(false);
            }
        });
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(etEnrolSearch, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }
}