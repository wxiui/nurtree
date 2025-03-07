package com.csstj.nurtree.service;

import com.csstj.nurtree.common.consts.AppConst;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterService {

    private static final String REGISTER_URL = AppConst.REGISTER;

    public void register(String username, String password,String childUsername, String childPassword, final RegCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // 创建 JSON 格式的请求体
        // 创建JsonObject对象
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("childUsername", childUsername);
        jsonObject.addProperty("childPassword", childPassword);
        // 设置MediaType
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // 创建RequestBody对象
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);

        // 创建请求
        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(requestBody)
                .build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 请求成功
                    String responseData = response.body().string();
                    callback.onSuccess(responseData);
                } else {
                    // 请求失败
                    callback.onFailure(new IOException("Unexpected code " + response.code()));
                }
            }
        });
    }

    public interface RegCallback {
        void onSuccess(String response);
        void onFailure(Throwable t);
    }
}
