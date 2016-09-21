package com.jkb.mrcampus.fragment.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkb.core.contract.comment.list.CommentListContract;
import com.jkb.core.contract.dynamicDetail.data.comment.DynamicDetailCommentData;
import com.jkb.mrcampus.Config;
import com.jkb.mrcampus.R;
import com.jkb.mrcampus.activity.CommentActivity;
import com.jkb.mrcampus.adapter.recycler.comment.CommentListAdapter;
import com.jkb.mrcampus.adapter.recycler.comment.CommentReplyAdapter;
import com.jkb.mrcampus.base.BaseFragment;

import java.util.List;

/**
 * 评论列表的View
 * Created by JustKiddingBaby on 2016/9/20.
 */

public class CommentListFragment extends BaseFragment implements CommentListContract.View,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private int dynamic_id = -1;

    private CommentListFragment(int dynamic_id) {
        this.dynamic_id = dynamic_id;
    }

    public CommentListFragment() {
    }

    private static CommentListFragment INSTANCE = null;

    public static CommentListFragment newInstance(int dynamic_id) {
        if (INSTANCE == null || dynamic_id > 0) {
            INSTANCE = new CommentListFragment(dynamic_id);
        }
        return INSTANCE;
    }

    //View
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    //评论
    private EditText etCommentInput;
    private TextView tvCommentCount;
    private TextView tvCommentRemainderCount;
    private ImageView ivSendComment;

    //Data
    private CommentActivity commentActivity;
    private CommentListContract.Presenter mPresenter;
    private CommentListAdapter commentListAdapter;
    private static final int MAX_COMMENT_COUNT = 360;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        commentActivity = (CommentActivity) mActivity;
        setRootView(R.layout.frg_comment_list);
        init(savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
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
        refreshLayout.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(onScrollListener);//设置滑动监听，设置是否下拉刷新

        //评论
        commentListAdapter.setOnLikeClickListener(onLikeClickListener);
        commentListAdapter.setOnHeadImgClickListener(onHeadImgClickListener);
        commentListAdapter.setOnReplyUserClickListener(onReplyUserClickListener);
        commentListAdapter.setOnTargetReplyUserClickListener(onTargetReplyUserClickListener);
        commentListAdapter.setOnViewAllCommentClickListener(onViewAllCommentClickListener);

        rootView.findViewById(R.id.fcl_ll_sendComment).setOnClickListener(this);
        rootView.findViewById(R.id.ts4_ib_left).setOnClickListener(this);

        //设置评论的内容数目的监听器
        etCommentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int nSelLength = etCommentInput.getText().toString().length();
                if (nSelLength > 0) {
                    ivSendComment.setImageResource(R.drawable.ic_comment_send);
                } else {
                    ivSendComment.setImageResource(R.drawable.ic_comment_send_gray);
                }
                tvCommentCount.setText(nSelLength + "");
                tvCommentRemainderCount.setText(String.valueOf(String
                        .valueOf(MAX_COMMENT_COUNT - nSelLength)));
            }

            @Override
            public void afterTextChanged(Editable s) {
                int nSelStart;
                int nSelEnd;
                nSelStart = etCommentInput.getSelectionStart();
                nSelEnd = etCommentInput.getSelectionEnd();
                boolean nOverMaxLength;
                nOverMaxLength = (s.length() > MAX_COMMENT_COUNT);
                if (nOverMaxLength) {
                    s.delete(nSelStart - 1, nSelEnd);
                    etCommentInput.setTextKeepState(s);
                }
            }
        });
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {

        } else {
            dynamic_id = savedInstanceState.getInt(Config.INTENT_KEY_DYNAMIC_ID);
        }
        commentListAdapter = new CommentListAdapter(mActivity, null);
        recyclerView.setAdapter(commentListAdapter);
    }

    @Override
    protected void initView() {
        //设置标题栏
        ((TextView) rootView.findViewById(R.id.ts4_tv_name)).setText("评论");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fcl_rv);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fcl_srl);

        //初始化输入
        etCommentInput = (EditText) rootView.findViewById(R.id.fcl_et_commentInput);
        //初始化其他
        tvCommentCount = (TextView) rootView.findViewById(R.id.fcl_tv_inputCount);
        tvCommentRemainderCount = (TextView) rootView.findViewById(R.id.fcl_tv_inputRemainderCount);
        ivSendComment = (ImageView) rootView.findViewById(R.id.fcl_iv_sendComment);
    }

    @Override
    public int bindDynamicId() {
        return dynamic_id;
    }

    @Override
    public void showRefreshView() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshView() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void setCommentData(List<DynamicDetailCommentData> commentsData) {
        if (commentsData == null || commentsData.size() <= 0) {
            return;
        }
        commentListAdapter.commentDataList = commentsData;
        commentListAdapter.notifyDataSetChanged();
    }

    @Override
    public void commitComment() {
        String comment = etCommentInput.getText().toString();
        mPresenter.sendComment(comment);
    }

    @Override
    public void clearComment$HideSoftInputView() {
        etCommentInput.setText("");//清楚文本框信息
        commentActivity.hideSoftKeyboard(etCommentInput);//设置软键盘不可见
    }

    @Override
    public void startPersonCenterView(int user_id) {
        commentActivity.startPersonalCenterActivity(user_id);
    }

    @Override
    public void showViewAllComment$ReplyView(int comment_id) {
        //显示所有的评论和回复的页面
        commentActivity.startCommentActivity(comment_id,
                CommentActivity.ACTION_SHOW_VIEW_COMMENT_SINGLE_ALL);
    }

    @Override
    public void setPresenter(CommentListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading(String value) {
        commentActivity.showLoading(value);
    }

    @Override
    public void dismissLoading() {
        commentActivity.dismissLoading();
    }

    @Override
    public void showReqResult(String value) {
        commentActivity.showShortToast(value);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Config.INTENT_KEY_DYNAMIC_ID, dynamic_id);
    }

    /**
     * 设置点击喜欢的点击事件监听
     */
    private CommentListAdapter.OnLikeClickListener onLikeClickListener = new
            CommentListAdapter.OnLikeClickListener() {
                @Override
                public void onLikeClick(int position) {
                    mPresenter.onLikeCommentClick(position);
                }
            };
    /**
     * 设置点击喜欢的点击事件监听
     */
    private CommentListAdapter.OnHeadImgClickListener onHeadImgClickListener = new
            CommentListAdapter.OnHeadImgClickListener() {
                @Override
                public void onHeadImgClick(int position) {
                    mPresenter.onCommentUserClick(position);
                }
            };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fcl_ll_sendComment:
                commitComment();
                break;
            case R.id.ts4_ib_left:
                commentActivity.onBackPressed();
                break;
        }
    }

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
    /**
     * 回复的目标用户点击回调
     */
    private CommentReplyAdapter.OnTargetReplyUserClickListener onTargetReplyUserClickListener =
            new CommentReplyAdapter.OnTargetReplyUserClickListener() {
                @Override
                public void onTargetReplyUserClick(int parentPosition, int position) {
                    Log.d(TAG, "parentPosition=" + parentPosition + "\n"
                            + "position=" + position);
                    mPresenter.onTargetReplyUserClick(parentPosition, position);
                }
            };
    /**
     * 回复的用户点击回调
     */
    private CommentReplyAdapter.OnReplyUserClickListener onReplyUserClickListener =
            new CommentReplyAdapter.OnReplyUserClickListener() {
                @Override
                public void onReplyUserClick(int parentPosition, int position) {
                    Log.d(TAG, "parentPosition=" + parentPosition + "\n"
                            + "position=" + position);
                    mPresenter.onReplyUserClick(parentPosition, position);
                }
            };
    /**
     * 查看所有回复的点击回调
     */
    private CommentListAdapter.OnViewAllCommentClickListener onViewAllCommentClickListener =
            new CommentListAdapter.OnViewAllCommentClickListener() {
                @Override
                public void OnViewAllCommentClick(int position) {
                    mPresenter.onViewAllComment$ReplyClick(position);
                }
            };
}
