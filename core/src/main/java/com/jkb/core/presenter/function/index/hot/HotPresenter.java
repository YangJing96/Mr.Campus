package com.jkb.core.presenter.function.index.hot;

import com.jkb.api.ApiCallback;
import com.jkb.api.ApiResponse;
import com.jkb.api.config.Config;
import com.jkb.api.entity.dynamic.DynamicPopularListEntity;
import com.jkb.api.entity.operation.OperationActionEntity;
import com.jkb.core.contract.function.index.HotContract;
import com.jkb.core.control.userstate.LoginContext;
import com.jkb.core.control.userstate.LogoutState;
import com.jkb.core.data.dynamic.circle.DynamicInCircle;
import com.jkb.core.data.index.dynamic.IndexDynamicData;
import com.jkb.core.data.index.dynamic.original.IndexOriginalArticleDynamicData;
import com.jkb.core.data.index.dynamic.original.IndexOriginalDynamicData;
import com.jkb.core.data.index.dynamic.original.IndexOriginalNormalDynamicData;
import com.jkb.core.data.index.dynamic.original.IndexOriginalTopicDynamicData;
import com.jkb.core.data.index.dynamic.unOriginal.favorite.IndexFavoriteArticleDynamicData;
import com.jkb.core.data.index.dynamic.unOriginal.favorite.IndexFavoriteDynamicData;
import com.jkb.core.data.index.dynamic.unOriginal.favorite.IndexFavoriteNormalDynamicData;
import com.jkb.core.data.index.dynamic.unOriginal.favorite.IndexFavoriteTopicDynamicData;
import com.jkb.core.data.index.dynamic.unOriginal.postInCircle.IndexCircleDynamicData;
import com.jkb.core.data.index.hot.HotDynamic;
import com.jkb.core.data.index.hot.circle.CircleDynamic;
import com.jkb.core.data.index.hot.dynamic.circle.CircleArticleDynamic;
import com.jkb.core.data.index.hot.dynamic.circle.CircleNormalDynamic;
import com.jkb.core.data.index.hot.dynamic.circle.CircleTopicDynamic;
import com.jkb.core.data.index.hot.dynamic.circle.DynamicInCircleDynamic;
import com.jkb.core.data.index.hot.dynamic.original.OriginalArticleDynamic;
import com.jkb.core.data.index.hot.dynamic.original.OriginalDynamic;
import com.jkb.core.data.index.hot.dynamic.original.OriginalNormalDynamic;
import com.jkb.core.data.index.hot.dynamic.original.OriginalTopicDynamic;
import com.jkb.core.data.index.hot.user.UserDynamic;
import com.jkb.core.data.info.circle.CircleInfo;
import com.jkb.core.data.info.dynamic.content.DynamicContentArticleInfo;
import com.jkb.core.data.info.dynamic.content.DynamicContentNormalInfo;
import com.jkb.core.data.info.dynamic.content.DynamicContentTopicInfo;
import com.jkb.core.data.info.user.UserInfo;
import com.jkb.model.data.PageControlEntity;
import com.jkb.model.dataSource.function.index.hot.DynamicHotDataRepository;
import com.jkb.model.info.SchoolInfoSingleton;
import com.jkb.model.info.UserInfoSingleton;
import com.jkb.model.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import jkb.mrcampus.db.entity.UserAuths;
import retrofit2.Response;

/**
 * 热门动态的P层
 * Created by JustKiddingBaby on 2016/9/30.
 */

public class HotPresenter implements HotContract.Presenter {

    //data
    private static final String TAG = "HotPresenter";
    private HotContract.View view;
    private DynamicHotDataRepository repository;

    //数据
    private int school_id = -1;
    private boolean isCached;
    private boolean isRefreshing;
    private List<HotDynamic> hotDynamics;
    //分页数据
    private PageControlEntity pageControl;

    public HotPresenter(HotContract.View view, DynamicHotDataRepository repository) {
        this.view = view;
        this.repository = repository;

        hotDynamics = new ArrayList<>();
        pageControl = new PageControlEntity();

        this.view.setPresenter(this);
    }

    @Override
    public void initSchoolId() {
        SchoolInfoSingleton schoolInfo = SchoolInfoSingleton.getInstance();
        if (!schoolInfo.isSelectedSchool()) {
            view.hideHotView();
        } else {
            school_id = schoolInfo.getSchool().getSchool_id();
            view.showHotView();
        }
    }

    @Override
    public void initHotDynamic() {
        initSchoolId();
        if (school_id <= 0) {
            return;
        }
        if (isCached) {
            bindDataToView();
            return;
        }
        onRefresh();
    }

    @Override
    public void bindDataToView() {
        isCached = true;
        isRefreshing = false;
        if (!view.isActive()) {
            return;
        }
        view.dismissLoading();
        view.hideRefreshingView();
        view.setHotDynamicData(hotDynamics);
    }

    @Override
    public void onRefresh() {
        if (isRefreshing) {
            return;
        }
        isRefreshing = true;
        view.showRefreshingView();
        hotDynamics.clear();
        pageControl.setCurrent_page(1);
        reqHotDynamicData();
    }


    @Override
    public void onLoadMore() {
        if (isRefreshing) {
            return;
        }
        if (pageControl.getCurrent_page() >= pageControl.getLast_page()) {
            return;
        }
        isRefreshing = true;
        pageControl.setCurrent_page(pageControl.getCurrent_page() + 1);
        reqHotDynamicData();
    }

    @Override
    public void onHotDynamicItemClick(int position) {
        HotDynamic hotDynamic = hotDynamics.get(position).getHotDynamic();
        if (hotDynamic instanceof UserDynamic) {//用户
            onUserItemClick(hotDynamic);
        } else if (hotDynamic instanceof CircleDynamic) {//圈子
            onCircleItemClick(hotDynamic);
        } else if (hotDynamic instanceof OriginalArticleDynamic) {//动态：文章
            onDynamicArticleClick(hotDynamic);
        } else if (hotDynamic instanceof OriginalNormalDynamic) {//动态：普通
            onDynamicNormalClick(hotDynamic);
        } else if (hotDynamic instanceof OriginalTopicDynamic) {//动态：话题
            onDynamicTopicClick(hotDynamic);
        } else if (hotDynamic instanceof DynamicInCircleDynamic) {//圈子内动态
            onDynamicInCircleClick(hotDynamic);
        }
    }


    @Override
    public void onUserAttentionItemClick(final int position) {
        if (!LoginContext.getInstance().isLogined()) {
            view.showReqResult("请先去登录再进行操作");
            return;
        }
        //请求订阅操作
        UserDynamic userDynamic = (UserDynamic) hotDynamics.get(position);
        int user_id = userDynamic.getUser_id();
        UserAuths userAuths = getUserAuths();
        int userId = userAuths.getUser_id();
        String Authorization = Config.HEADER_BEARER + userAuths.getToken();
        view.showLoading("");
        repository.payAttentionUser(Authorization, userId, user_id,
                new ApiCallback<ApiResponse<OperationActionEntity>>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<OperationActionEntity>> response) {
                        if (view.isActive()) {
                            view.showReqResult("操作成功");
                            UserDynamic user = (UserDynamic) hotDynamics.get(position);
                            boolean has_attention = user.isHas_attention();
                            has_attention = !has_attention;
                            user.setHas_attention(has_attention);
                            bindDataToView();
                        }
                    }

                    @Override
                    public void onError(Response<ApiResponse<OperationActionEntity>> response,
                                        String error, ApiResponse<OperationActionEntity> apiResponse) {
                        if (view.isActive()) {
                            view.showReqResult(error);
                        }
                    }

                    @Override
                    public void onFail() {
                        if (view.isActive()) {
                            view.showReqResult("操作失败");
                        }
                    }
                });
    }

    @Override
    public void onCircleSubscribeItemClick(final int position) {
        if (!LoginContext.getInstance().isLogined()) {
            view.showReqResult("请先去登录再进行操作");
            return;
        }
        //请求订阅操作
        CircleDynamic circleDynamic = (CircleDynamic) hotDynamics.get(position);
        int circle_id = circleDynamic.getCircle_id();
        UserAuths userAuths = getUserAuths();
        int userId = userAuths.getUser_id();
        String Authorization = Config.HEADER_BEARER + userAuths.getToken();
        view.showLoading("");
        repository.subscribeCircle(Authorization, userId, circle_id,
                new ApiCallback<ApiResponse<OperationActionEntity>>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<OperationActionEntity>> response) {
                        if (view.isActive()) {
                            view.showReqResult("操作成功");
                            CircleDynamic circle = (CircleDynamic) hotDynamics.get(position);
                            boolean has_subscribe = circle.isHas_subscribe();
                            has_subscribe = !has_subscribe;
                            circle.setHas_subscribe(has_subscribe);
                            bindDataToView();
                        }
                    }

                    @Override
                    public void onError(Response<ApiResponse<OperationActionEntity>> response,
                                        String error, ApiResponse<OperationActionEntity> apiResponse) {
                        if (view.isActive()) {
                            view.showReqResult(error);
                        }
                    }

                    @Override
                    public void onFail() {
                        if (view.isActive()) {
                            view.showReqResult("操作失败");
                        }
                    }
                });
    }

    @Override
    public void onHeadImgItemClick(int position) {
        HotDynamic hotDynamic = hotDynamics.get(position).getHotDynamic();
        if (hotDynamic instanceof OriginalDynamic) {
            onUserHeadImgClick(hotDynamic);
        } else if (hotDynamic instanceof DynamicInCircleDynamic) {
            onCircleHeadImgClick(hotDynamic);
        }
    }

    @Override
    public void onCommentItemClick(int position) {
        HotDynamic hotDynamic = hotDynamics.get(position).getHotDynamic();
        if (hotDynamic instanceof OriginalDynamic) {
            onOriginalCommentClick(hotDynamic);
        } else if (hotDynamic instanceof DynamicInCircleDynamic) {
            onDynamicInCircleCommentClick(hotDynamic);
        }
    }

    @Override
    public void onLikeItemClick(final int position) {
        if (!LoginContext.getInstance().isLogined()) {
            view.showReqResult("请先去登录再进行操作");
            return;
        }
        int dynamic_id = -1;
        final HotDynamic hotDynamic = hotDynamics.get(position).getHotDynamic();
        if (hotDynamic instanceof OriginalDynamic) {
            dynamic_id = ((OriginalDynamic) hotDynamic).getDynamic_id();
        } else if (hotDynamic instanceof DynamicInCircleDynamic) {
            dynamic_id = ((DynamicInCircleDynamic) hotDynamic).getDynamic_id();
        }
        //请求喜欢操作
        UserAuths userAuths = getUserAuths();
        int userId = userAuths.getUser_id();
        String Authorization = Config.HEADER_BEARER + userAuths.getToken();
        repository.favorite(Authorization, userId, dynamic_id,
                new ApiCallback<ApiResponse<OperationActionEntity>>() {
                    @Override
                    public void onSuccess(Response<ApiResponse<OperationActionEntity>> response) {
                        if (view.isActive()) {
                            view.showReqResult("操作成功");
                            HotDynamic hotDynamic = hotDynamics.get(position).getHotDynamic();
                            if (hotDynamic instanceof OriginalDynamic) {
                                boolean has_favorite = ((OriginalDynamic) hotDynamic).isHas_favorite();
                                has_favorite = !has_favorite;
                                ((OriginalDynamic) hotDynamic).setHas_favorite(has_favorite);
                                int count_of_favorite = ((OriginalDynamic) hotDynamic).getCount_of_favorite();
                                if (has_favorite) {
                                    count_of_favorite += 1;
                                } else {
                                    count_of_favorite -= 1;
                                }
                                ((OriginalDynamic) hotDynamic).setCount_of_favorite(count_of_favorite);
                            } else if (hotDynamic instanceof DynamicInCircleDynamic) {
                                boolean has_favorite = ((DynamicInCircleDynamic) hotDynamic).isHas_favorite();
                                has_favorite = !has_favorite;
                                ((DynamicInCircleDynamic) hotDynamic).setHas_favorite(has_favorite);
                                int count_of_favorite = ((DynamicInCircleDynamic) hotDynamic).getCount_of_favorite();
                                if (has_favorite) {
                                    count_of_favorite += 1;
                                } else {
                                    count_of_favorite -= 1;
                                }
                                ((DynamicInCircleDynamic) hotDynamic).setCount_of_favorite(count_of_favorite);
                            }
                            bindDataToView();
                        }
                    }

                    @Override
                    public void onError(Response<ApiResponse<OperationActionEntity>> response,
                                        String error, ApiResponse<OperationActionEntity> apiResponse) {
                        if (view.isActive()) {
                            view.showReqResult(error);
                        }
                    }

                    @Override
                    public void onFail() {
                        if (view.isActive()) {
                            view.showReqResult("操作失败");
                        }
                    }
                });
    }

    @Override
    public void onShareItemClick(int position) {
        HotDynamic dynamicData = hotDynamics.get(position);
        String title = getShareTitle(dynamicData);
        String pictureUrl = getSharePicture(dynamicData);
        String contentText = getShareText(dynamicData);
        String url = Config.APP_DOWNLOAD_ADDRESS;
        view.share(title, url, contentText, pictureUrl, url, "校园菌菌", url);
    }

    //得到分享的内容
    private String getShareText(HotDynamic dynamicData) {
        String shareText = null;
        if (dynamicData instanceof DynamicInCircleDynamic) {
            shareText = "我在菌菌向您分享了一条圈子动态";
        } else if (dynamicData instanceof OriginalDynamic) {
            shareText = "我在菌菌向您分享了一条动态";
        }
        return shareText;
    }

    private String getSharePicture(HotDynamic dynamicData) {
        String sharePicture = null;
        if (dynamicData instanceof OriginalDynamic) {
            sharePicture = getOriginalDynamicPicture((OriginalDynamic) dynamicData);
        } else if (dynamicData instanceof DynamicInCircleDynamic) {
            sharePicture = getCircleDynamicPicture((DynamicInCircleDynamic) dynamicData);
        }
        return sharePicture;
    }

    //得到圈子的动态图片
    private String getCircleDynamicPicture(DynamicInCircleDynamic dynamicData) {
        return dynamicData.getCircle().getPictureUrl();
    }

    //得到原创的动态图片
    private String getOriginalDynamicPicture(OriginalDynamic dynamicData) {
        String pictureUrl = null;
        if (dynamicData instanceof OriginalNormalDynamic) {
        } else if (dynamicData instanceof OriginalArticleDynamic) {
            List<OriginalArticleDynamic.ArticleContent> articleContents =
                    ((OriginalArticleDynamic) dynamicData).getArticleContents();
            if (articleContents == null || articleContents.size() == 0) {
                pictureUrl = null;
            } else {
                pictureUrl = articleContents.get(0).getImg();
            }
        } else if (dynamicData instanceof OriginalTopicDynamic) {
            OriginalTopicDynamic.TopicContent topicContent = ((OriginalTopicDynamic) dynamicData).getTopicContent();
            pictureUrl = topicContent.getImg();
        }
        return pictureUrl;
    }

    //得到分享的标题
    private String getShareTitle(HotDynamic dynamicData) {
        String shareTitle = null;
        if (dynamicData instanceof OriginalDynamic) {
            shareTitle = ((OriginalDynamic) dynamicData).getTitle();
        } else if (dynamicData instanceof DynamicInCircleDynamic) {
            shareTitle = ((DynamicInCircleDynamic) dynamicData).getTitle();
        }
        return shareTitle;
    }

    @Override
    public void onCreatorUserClick(int position) {
        HotDynamic hotDynamic = hotDynamics.get(position);
        if (hotDynamic instanceof DynamicInCircleDynamic) {
            DynamicInCircleDynamic dynamicInCircleDynamic = (DynamicInCircleDynamic) hotDynamic;
            UserInfo user = dynamicInCircleDynamic.getUser();
            int id = user.getId();
            view.startPersonCenter(id);
        }
    }

    @Override
    public void setCacheExpired() {
        isCached = false;
    }

    @Override
    public void onPicturesClick(int position, int clickPosition) {
        //点击大图的时候
        HotDynamic dynamicData = hotDynamics.get(position);
        if (dynamicData instanceof OriginalArticleDynamic) {
            handlePictureBrowserArticle((OriginalArticleDynamic) dynamicData);
        } else if (dynamicData instanceof OriginalTopicDynamic) {
            handlePictureBrowserTopic((OriginalTopicDynamic) dynamicData);
        } else if (dynamicData instanceof OriginalNormalDynamic) {
            handlePictureBrowserNormal((OriginalNormalDynamic) dynamicData, clickPosition);
        }
    }

    /**
     * 处理大图预览的动态条目
     *
     * @param dynamicData
     * @param clickPosition 被点击的图片条目
     */
    private void handlePictureBrowserNormal(OriginalNormalDynamic dynamicData,
                                            int clickPosition) {
        /*DynamicContentNormalInfo normal = dynamicData.getNormal();
        if (normal == null) {
            return;
        }
        List<String> img = normal.getImg();
        view.showImagesBrowserView((ArrayList<String>) img, clickPosition);*/
    }

    /**
     * 处理大图预览的话题动态
     */
    private void handlePictureBrowserTopic(OriginalTopicDynamic dynamicData) {
        OriginalTopicDynamic.TopicContent topic = dynamicData.getTopicContent();
        if (topic == null) {
            return;
        }
        String img = topic.getImg();
        if (!StringUtils.isEmpty(img)) {
            ArrayList<String> arr = new ArrayList<>();
            arr.add(img);
            view.showImagesBrowserView(arr, 0);
        }
    }

    /**
     * 处理大图预览的文章动态
     */
    private void handlePictureBrowserArticle(OriginalArticleDynamic dynamicData) {
        DynamicContentArticleInfo article = (DynamicContentArticleInfo)
                dynamicData.getArticleContents();
        if (article != null) {
            List<DynamicContentArticleInfo.Article> articles = article.getArticle();
            if (articles == null || articles.size() == 0) {
                return;
            }
            for (DynamicContentArticleInfo.Article art :
                    articles) {
                if (!StringUtils.isEmpty(art.getImg())) {
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add(art.getImg());
                    view.showImagesBrowserView(arr, 0);
                }
            }
        }
    }

    @Override
    public void start() {
        initHotDynamic();
    }

    /**
     * 当文章动态条目被点击的时候
     */
    private void onDynamicArticleClick(HotDynamic hotDynamic) {
        OriginalArticleDynamic articleDynamic = (OriginalArticleDynamic) hotDynamic;
        int dynamic_id = articleDynamic.getDynamic_id();
        view.startDynamicDetail(dynamic_id, Config.D_TYPE_ARTICLE);
    }

    /**
     * 当文章动态条目被点击的时候
     */
    private void onDynamicNormalClick(HotDynamic hotDynamic) {
    }

    /**
     * 当话题动态条目被点击的时候
     */
    private void onDynamicTopicClick(HotDynamic hotDynamic) {
        OriginalTopicDynamic topicDynamic = (OriginalTopicDynamic) hotDynamic;
        int dynamic_id = topicDynamic.getDynamic_id();
        view.startDynamicDetail(dynamic_id, Config.D_TYPE_TOPIC);
    }

    /**
     * 圈子内动态点击
     */
    private void onDynamicInCircleClick(HotDynamic hotDynamic) {
        DynamicInCircleDynamic dynamic = (DynamicInCircleDynamic) hotDynamic;
        int dynamic_id = dynamic.getDynamic_id();
        if (hotDynamic instanceof CircleArticleDynamic) {
            view.startDynamicDetail(dynamic_id, Config.D_TYPE_ARTICLE);
        } else if (hotDynamic instanceof CircleNormalDynamic) {
            view.startDynamicDetail(dynamic_id, Config.D_TYPE_NORMAL);
        } else if (hotDynamic instanceof CircleTopicDynamic) {
            view.startDynamicDetail(dynamic_id, Config.D_TYPE_TOPIC);
        }
    }

    /**
     * 当圈子条目被点击的时候
     */
    private void onCircleItemClick(HotDynamic hotDynamic) {
        CircleDynamic circleDynamic = (CircleDynamic) hotDynamic;
        int circle_id = circleDynamic.getCircle_id();
        view.startCircleIndex(circle_id);
    }

    /**
     * 用户条目被点击的时候
     */
    private void onUserItemClick(HotDynamic hotDynamic) {
        UserDynamic userDynamic = (UserDynamic) hotDynamic;
        int user_id = userDynamic.getUser_id();
        view.startPersonCenter(user_id);
    }

    /**
     * 用户头像被点击的时候
     */
    private void onUserHeadImgClick(HotDynamic hotDynamic) {
        OriginalDynamic originalDynamic = (OriginalDynamic) hotDynamic;
        UserInfo user = originalDynamic.getUser();
        int id = user.getId();
        view.startPersonCenter(id);
    }

    /**
     * 圈子头像被点击的时候
     */
    private void onCircleHeadImgClick(HotDynamic hotDynamic) {
        DynamicInCircleDynamic dynamicInCircleDynamic = (DynamicInCircleDynamic) hotDynamic;
        CircleInfo circle = dynamicInCircleDynamic.getCircle();
        int circleId = circle.getCircleId();
        view.startCircleIndex(circleId);
    }

    /**
     * 圈子内动态的评论点击事件
     */
    private void onDynamicInCircleCommentClick(HotDynamic hotDynamic) {
        DynamicInCircleDynamic dynamicInCircleDynamic = (DynamicInCircleDynamic) hotDynamic;
        int dynamic_id = dynamicInCircleDynamic.getDynamic_id();
        view.startCommentList(dynamic_id);
    }

    /**
     * 原创动态的评论点击事件
     */
    private void onOriginalCommentClick(HotDynamic hotDynamic) {
        OriginalDynamic originalDynamic = (OriginalDynamic) hotDynamic;
        int dynamic_id = originalDynamic.getDynamic_id();
        view.startCommentList(dynamic_id);
    }

    /**
     * 请求热门动态的数据
     */
    private void reqHotDynamicData() {
        isCached = false;
        String Authorization;
        if (LoginContext.getInstance().isLogined()) {
            UserAuths userAuths = getUserAuths();
            Authorization = Config.HEADER_BEARER + userAuths.getToken();
        } else {
            Authorization = null;
        }
        repository.getAllHotDynamic(Authorization, school_id, pageControl.getCurrent_page(),
                hotDynamicListApiCallback);
    }

    /**
     * 获取热门动态列表的回调
     */
    private ApiCallback<ApiResponse<DynamicPopularListEntity>> hotDynamicListApiCallback = new
            ApiCallback<ApiResponse<DynamicPopularListEntity>>() {
                @Override
                public void onSuccess(Response<ApiResponse<DynamicPopularListEntity>> response) {
                    if (view.isActive()) {
                        handleData(response.body());
                    }
                }

                /**
                 * 解析数据
                 */
                private void handleData(ApiResponse<DynamicPopularListEntity> body) {
                    if (body == null) {
                        hotDynamics.clear();
                        bindDataToView();
                        return;
                    }
                    handleHotData(body.getMsg());
                }

                /**
                 * 解析热门数据列表
                 */
                private void handleHotData(DynamicPopularListEntity msg) {
                    if (msg == null) {
                        hotDynamics.clear();
                        bindDataToView();
                        return;
                    }

                    //设置页码控制器
                    pageControl.setTotal(msg.getTotal());
                    pageControl.setPer_page(msg.getPer_page());
                    pageControl.setCurrent_page(msg.getCurrent_page());
                    pageControl.setLast_page(msg.getLast_page());
                    pageControl.setNext_page_url(msg.getNext_page_url());
                    pageControl.setPrev_page_url(msg.getPrev_page_url());
                    pageControl.setFrom(msg.getFrom());
                    pageControl.setTo(msg.getTo());

                    List<DynamicPopularListEntity.DataBean> data = msg.getData();
                    if (data == null || data.size() == 0) {
                        hotDynamics.clear();
                        bindDataToView();
                        return;
                    }
                    for (int i = 0; i < data.size(); i++) {
                        DynamicPopularListEntity.DataBean dataBean = data.get(i);
                        if (dataBean == null) {
                            continue;
                        }
                        //判断类型
                        HotDynamic hotDynamic = new HotDynamic(dataBean).getHotDynamic();
                        if (hotDynamic != null) {
                            hotDynamics.add(hotDynamic);
                        }
                    }
                    bindDataToView();
                }

                @Override
                public void onError(Response<ApiResponse<DynamicPopularListEntity>> response,
                                    String error, ApiResponse<DynamicPopularListEntity> apiResponse) {
                    if (view.isActive()) {
                        view.showReqResult(error);
                        bindDataToView();
                    }
                }

                @Override
                public void onFail() {
                    if (view.isActive()) {
                        bindDataToView();
                    }
                }
            };

    /**
     * 得到用户数据
     */
    private UserAuths getUserAuths() {
        UserInfoSingleton userInfo = UserInfoSingleton.getInstance();
        UserAuths users = userInfo.getUserAuths();
        if (users == null) {
            LoginContext.getInstance().setUserState(new LogoutState());
            view.showReqResult("登录过期，请重新登录~");
        }
        return users;
    }
}
