package com.jkb.mrcampus.fragment.personCenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jkb.core.contract.circle.CircleUserInCircleBlackListContract;
import com.jkb.core.data.circle.userInCircle.UserInCircle;
import com.jkb.mrcampus.Config;
import com.jkb.mrcampus.R;
import com.jkb.mrcampus.activity.CircleActivity;
import com.jkb.mrcampus.adapter.recycler.circle.userInCircle.UsersInCircleAdapter;
import com.jkb.mrcampus.base.BaseFragment;

import java.util.List;

/**
 * 禁闭室页面
 * Created by JustKiddingBaby on 2016/11/3.
 */

public class CircleUserInCircleBlackListFragment extends BaseFragment implements
        CircleUserInCircleBlackListContract.View,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static CircleUserInCircleBlackListFragment newInstance(
            @NonNull int circle_id) {
        CircleUserInCircleBlackListFragment INSTANCE = new CircleUserInCircleBlackListFragment();
        Bundle args = new Bundle();
        args.putInt(Config.INTENT_KEY_CIRCLE_ID, circle_id);
        INSTANCE.setArguments(args);
        return INSTANCE;
    }

    //data
    private int circle_id;
    private CircleActivity circleActivity;
    private CircleUserInCircleBlackListContract.Presenter mPresenter;

    //用户列表
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private UsersInCircleAdapter usersInCircleAdapter;

    //view
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        circleActivity = (CircleActivity) mActivity;
        setRootView(R.layout.frg_circle_attention_user_list);
        super.onCreateView(inflater, container, savedInstanceState);
        init(savedInstanceState);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.start();
        }
    }

    @Override
    protected void initListener() {
        rootView.findViewById(R.id.ts4_ib_left).setOnClickListener(this);

        refreshLayout.setOnRefreshListener(this);
        usersInCircleAdapter.setOnUserInCircleItemClickListener(onUserInCircleItemClickListener);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            circle_id = args.getInt(Config.INTENT_KEY_CIRCLE_ID);
        } else {
            circle_id = savedInstanceState.getInt(Config.INTENT_KEY_CIRCLE_ID);
        }
        usersInCircleAdapter = new UsersInCircleAdapter(context, null);
        recyclerView.setAdapter(usersInCircleAdapter);
    }

    @Override
    protected void initView() {
        //标题栏
        ((TextView) rootView.findViewById(R.id.ts4_tv_name)).setText("禁闭室");

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fcaul_srl);
        //用户列表
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fcaul_rv);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ts4_ib_left:
                circleActivity.onBackPressed();
                break;
        }
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Config.INTENT_KEY_CIRCLE_ID, circle_id);
    }

    @Override
    public int getCircleId() {
        return circle_id;
    }

    @Override
    public void setUserData(List<UserInCircle> userData, boolean isCreator) {
        usersInCircleAdapter.isCircleCreator = isCreator;
        usersInCircleAdapter.usersInCircles = userData;
        usersInCircleAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRefreshingView() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshingView() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void startPersonCenter(@NonNull int user_id) {
        circleActivity.startPersonalCenterActivity(user_id);
    }

    @Override
    public void setPresenter(CircleUserInCircleBlackListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading(String value) {
        circleActivity.showLoading(value, this);
    }

    @Override
    public void dismissLoading() {
        circleActivity.dismissLoading();
    }

    @Override
    public void showReqResult(String value) {
        showShortToash(value);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView = null;
        circleActivity = null;
        linearLayoutManager = null;
        refreshLayout = null;
        usersInCircleAdapter = null;
    }

    /**
     * 圈子成员的条目点击回调接口
     */
    private UsersInCircleAdapter.OnUserInCircleItemClickListener onUserInCircleItemClickListener =
            new UsersInCircleAdapter.OnUserInCircleItemClickListener() {
                @Override
                public void onUserItemClick(int position) {
                    mPresenter.onUserItemClick(position);
                }

                @Override
                public void onPull$PutBlackList(int position) {
                    mPresenter.onPull$PutBlackListItemClick(position);
                }
            };
    /**
     * 滑动的监听器
     */
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = (linearLayoutManager).findLastVisibleItemPosition();
            int totalItemCount = linearLayoutManager.getItemCount();
            if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                mPresenter.onLoadMore();//设置下拉加载
            }
        }
    };
}
