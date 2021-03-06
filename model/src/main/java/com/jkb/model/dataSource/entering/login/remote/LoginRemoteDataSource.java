package com.jkb.model.dataSource.entering.login.remote;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.jkb.api.ApiCallback;
import com.jkb.api.ApiEngine;
import com.jkb.api.ApiFactoryImpl;
import com.jkb.api.ApiResponse;
import com.jkb.api.entity.auth.LoginEntity;
import com.jkb.api.net.auth.LoginApi;
import com.jkb.model.dataSource.entering.login.LoginDataSource;
import com.jkb.model.dataSource.entering.login.ThirdPlatformData;
import com.jkb.model.net.ImageLoaderFactory;
import com.jkb.model.utils.StringUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.sharesdk.douban.Douban;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.renren.Renren;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import jkb.mrcampus.db.entity.Status;
import jkb.mrcampus.db.entity.UserAuths;
import jkb.mrcampus.db.entity.Users;
import retrofit2.Call;

/**
 * 登录的远程数据来源类
 * Created by JustKiddingBaby on 2016/8/3.
 */
public class LoginRemoteDataSource implements LoginDataSource {

    private static final String TAG = "LoginRemoteDataSource";

    private Context applicationContext;
    private static LoginRemoteDataSource INSTANCE;
    private String identity_type;

    public static LoginRemoteDataSource getInstance(Context applicationContext) {
        if (INSTANCE == null) {
            INSTANCE = new LoginRemoteDataSource(applicationContext);
        }
        return INSTANCE;
    }

    private LoginRemoteDataSource(Context applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void loginByQQ(ThirdPlatformListener listener) {
        identity_type = "qq";
        loginByThirdPlatform(QQ.NAME, listener);
    }

    @Override
    public void loginByWechat(ThirdPlatformListener listener) {
        identity_type = "wechat";
        loginByThirdPlatform(Wechat.NAME, listener);
    }

    @Override
    public void loginByWeibo(ThirdPlatformListener listener) {
        identity_type = "weibo";
        loginByThirdPlatform(SinaWeibo.NAME, listener);
    }

    @Override
    public void loginByRenRen(ThirdPlatformListener listener) {
        identity_type = "renren";
        loginByThirdPlatform(Renren.NAME, listener);
    }

    @Override
    public void loginByDouBan(ThirdPlatformListener listener) {
        identity_type = "douban";
        loginByThirdPlatform(Douban.NAME, listener);
    }

    @Override
    public void loginWithPhone(String phone, String passWord, ApiCallback<ApiResponse<LoginEntity>> apiCallback) {
        //请求登录接口
        ApiFactoryImpl apiFactory = ApiFactoryImpl.newInstance();
        apiFactory.setHttpClient(apiFactory.genericClient());
        apiFactory.initRetrofit();
        LoginApi loginApi = apiFactory.createApi(LoginApi.class);
        Call<ApiResponse<LoginEntity>> call;
        call = loginApi.loginWithPhone(phone, passWord);
        Type type = new TypeToken<ApiResponse<LoginEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<LoginEntity>>(apiCallback, call, type);
    }

    @Override
    public void loginWithEmail(String email, String passWord, ApiCallback<ApiResponse<LoginEntity>> apiCallback) {
        //请求登录接口
        ApiFactoryImpl apiFactory = ApiFactoryImpl.newInstance();
        apiFactory.setHttpClient(apiFactory.genericClient());
        apiFactory.initRetrofit();
        LoginApi loginApi = apiFactory.createApi(LoginApi.class);
        Call<ApiResponse<LoginEntity>> call;
        call = loginApi.loginWithEmail(email, passWord);
        Type type = new TypeToken<ApiResponse<LoginEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<LoginEntity>>(apiCallback, call, type);
    }

    @Override
    public void loginByThirdPlatform(
            @NonNull String nickname, @NonNull String identifier,
            @NonNull String identity_type, String sex, String avatar,
            String credential, String background, ApiCallback<ApiResponse<LoginEntity>> apiCallback) {
        //请求登录接口
        ApiFactoryImpl apiFactory = ApiFactoryImpl.newInstance();
        apiFactory.setHttpClient(apiFactory.genericClient());
        apiFactory.initRetrofit();
        LoginApi loginApi = apiFactory.createApi(LoginApi.class);
        Call<ApiResponse<LoginEntity>> call;
        call = loginApi.loginThirdPlatform(nickname, identifier, identity_type, sex, avatar
                , credential, background);
        Type type = new TypeToken<ApiResponse<LoginEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<LoginEntity>>(apiCallback, call, type);
    }

    @Override
    public void saveUserToDb(Users users) {

    }

    @Override
    public void saveUserAuthsToDb(UserAuths userAuths) {

    }

    @Override
    public void saveStatusToDb(Status status) {

    }

    @Override
    public void getUserAuthsDataFromDb(UserAuthsDataCallback callback) {

    }

    @Override
    public String getCurrentVersion() {
        return null;
    }

    @Override
    public void getBitmapFromUrl(String url, final BitmapDataCallback callback) {
        if (StringUtils.isEmpty(url)) {
            callback.onDataNotAvailable();
            return;
        }
        ImageLoaderFactory.getInstance().loadImage(url, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                callback.onDataNotAvailable();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                callback.onBitmapDataLoaded(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void cacheBitmapToFile(String path, String name, Bitmap bitmap, @NonNull BitmapToFileDataCallback callback) {

    }


    /**
     * 通过第三方登录
     *
     * @param name
     * @param listener
     */
    private void loginByThirdPlatform(String name, ThirdPlatformListener listener) {
        Platform platform = ShareSDK.getPlatform(applicationContext, name);
        platform.SSOSetting(false);
        platform.setPlatformActionListener(new PlatFormListener(listener));
        platform.authorize();
        platform.showUser(null);
    }

    /**
     * 第三方登录后的回调类
     */
    private class PlatFormListener implements PlatformActionListener {

        private ThirdPlatformListener thirdPlatformListener;

        public PlatFormListener(ThirdPlatformListener thirdPlatformListener) {
            this.thirdPlatformListener = thirdPlatformListener;
        }

        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
            Log.w(TAG, "hashMap=" + hashMap);
//            Iterator ite = hashMap.entrySet().iterator();
//            while (ite.hasNext()) {
//                Map.Entry entry = (Map.Entry) ite.next();
//                Object key = entry.getKey();
//                Object value = entry.getValue();
//                System.out.println(key + "： " + value);
//            }
            //用户资源都保存到res
            //通过打印res数据看看有哪些数据是你想要的
            Log.w(TAG, "action=" + action);
            switch (action) {
                case Platform.ACTION_USER_INFOR://获取用户数据
                    //在此处获取用户数据
                    seDbFromInfo(hashMap, platform);//获取用户数据成功
                    break;
                case Platform.ACTION_AUTHORIZING://进行点击操作以后
                    seDbFromInfo(hashMap, platform);//获取用户数据成功
                    if (thirdPlatformListener != null) {
                        thirdPlatformListener.onSuccess(getDataFromDb(platform));
                    }
                    break;
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            if (thirdPlatformListener != null) {
                thirdPlatformListener.onFail("登录失败");
            }
        }

        @Override
        public void onCancel(Platform platform, int i) {
            if (thirdPlatformListener != null) {
                thirdPlatformListener.onCancle();
            }
        }
    }

    /**
     * 得到用户数据
     */
    private void seDbFromInfo(HashMap<String, Object> hashMap, Platform platform) {
        if(hashMap==null){
            return;
        }
        String userIcon = null;
        if (identity_type.equals("qq")) {
            //解析qq数据
            Iterator ite = hashMap.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry entry = (Map.Entry) ite.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                switch (key.toString()) {
                    case "figureurl_2":
                        userIcon = value.toString();
                        break;
                }
            }
            if (userIcon != null) {
                platform.getDb().put("icon", userIcon);//设置为高清头像
            }
            Log.d(TAG, "userIcon=" + userIcon);
        }

    }

    /**
     * 通过第三方的数据库得到第三方数据对象
     *
     * @param platform
     * @return
     */
    private ThirdPlatformData getDataFromDb(Platform platform) {
        if (platform == null) {
            return null;
        }
        ThirdPlatformData data = new ThirdPlatformData();
        PlatformDb platDB = platform.getDb();//获取数平台数据DB
        if (platDB == null) {
            return null;
        }
        //通过DB获取各种数据
        String token = platDB.getToken();
        String gender = platDB.getUserGender();
        String userIcon = platDB.getUserIcon();
        String userId = platDB.getUserId();
        String userName = platDB.getUserName();
        Log.w(TAG, "token=" + token);
        Log.w(TAG, "gender=" + gender);
        Log.w(TAG, "userIcon=" + userIcon);
        Log.w(TAG, "userId=" + userId);
        Log.w(TAG, "userName=" + userName);
        data.setToken(token);
        data.setGender(gender);
        data.setIcon(userIcon);
        data.setNickname(userName);
        data.setUserID(userId);
        data.setIdentity_type(identity_type);
        return data;
    }
}
