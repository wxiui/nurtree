package com.csstj.nurtree.common.util;

import android.content.Context;
import android.content.Intent;

import com.csstj.nurtree.common.AppCodeManager;
import com.csstj.nurtree.ui.login.LoginActivity;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {
    private static OkHttpClient okHttpClient;
    private static Context context;

    private static AppCodeManager appCodeManager;

    public static void init(Context appContext) {
        context = appContext;
        appCodeManager = new AppCodeManager(context);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 添加拦截器处理 401 状态码
        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            // 在请求头中添加权限认证信息，这里假设使用 Bearer Token
            String token = getTokenFromLocal();
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + appCodeManager.getNickname())
                    .build();

            Response response = chain.proceed(newRequest);
            if (response.code() == 401) {
                // 跳转到登录界面
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                // 关闭响应体，避免资源泄漏
                response.close();
            }
            return response;
        };

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build();
    }

    private static String getTokenFromLocal() {
        // 从本地存储中获取 token，这里需要根据实际情况实现
        return "your_token_here";
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            throw new IllegalStateException("OkHttpUtils 还未初始化，请先调用 init 方法");
        }
        return okHttpClient;
    }

    public static void sendRequest(Request request, Callback callback) {
        getOkHttpClient().newCall(request).enqueue(callback);
    }
}