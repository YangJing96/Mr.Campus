package com.jkb.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * API层的引擎类
 * Created by JustKiddingBaby on 2016/7/31.
 */
public class ApiEngine<T> {

    private static final String TAG = "ApiEngine";
    private ApiCallback<T> apiCallback;
    private Call<T> call;
    private Type mType;

    public ApiEngine(@NonNull ApiCallback apiCallback, Call call, @NonNull Type mType) {
        Log.d(TAG, "ApiEngine");
        this.apiCallback = apiCallback;
        this.call = call;
        this.mType = mType;
        if (apiCallback == null) {
            return;
        }
        //开始请求
        start();
    }

    /**
     * 執行
     */
    private void start() {
        Log.d(TAG, "start");
        Observable.just(call)
                .map(new Func1<Call<T>, Response<T>>() {
                    @Override
                    public Response<T> call(Call<T> tCall) {
                        try {
                            Response<T> response = call.execute();
                            return response;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Response<T>>() {
                    @Override
                    public void call(Response<T> response) {
                        try {
                            handleResponse(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 处理数据
     *
     * @param response
     */
    private void handleResponse(Response<T> response) throws IOException {
        if (response == null) {
            apiCallback.onFail();
            return;
        }
//        Headers headers = response.headers();
//        //打印头信息
//        if (headers != null) {
//            for (int i = 0; i < headers.size(); i++) {
//                Log.i(TAG, headers.name(i) + "---->" + headers.value(i));
//            }
//        }
        int code = response.code();
        Log.d(TAG, code + "");
        switch (code) {
            case 200://成功
                Gson gson = new Gson();
                Log.i(TAG, gson.toJson(response.body()));
                apiCallback.onSuccess(response);
                break;
            case 404:
            case 422:
            default:
                String error = response.errorBody().string();
                Log.i(TAG, error);
                T obj = new Gson().fromJson(error, mType);
                apiCallback.onError(response, error, obj);
                break;
        }
    }
}