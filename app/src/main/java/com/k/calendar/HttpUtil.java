package com.k.calendar;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpUtil {
    private static final String TAG = "---ok---";
    private static OkHttpClient client;
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static OkHttpClient getClient() {
        if (client == null) {
            client = getDefaultOkHttpClient();
        }
        return client;
    }

    private static OkHttpClient getDefaultOkHttpClient() {
        X509TrustManager trustAllCert = new X509TrustManagerImpl();
        SSLSocketFactory sslSocketFactory = new SSLSocketFactoryImpl(trustAllCert);
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                //添加信任证书
                .sslSocketFactory(sslSocketFactory, trustAllCert)
                //忽略host验证
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    public static void postJson(String url, Object json, RequestListener listener) {
        postJson(url, null, json, listener);
    }

    public static void postJson(String url, Object json, String tag, RequestListener listener) {
        postJson(url, null, json, tag, listener);
    }

    public static void getRequest(String url, Map<String, Object> map, RequestListener listener) {
        Request.Builder builder = new Request.Builder();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (map != null) {
            for (Map.Entry<String, Object> param : map.entrySet()) {
                urlBuilder.addQueryParameter(param.getKey(), param.getValue().toString());
            }
        }
        builder.url(urlBuilder.build()).get();
        Request request = builder.build();
        callRequest(url, request, null, listener);
    }

    public static void postJson(String url, Headers header, Object json, RequestListener listener) {
        postJson(url, header, json, null, listener);
    }

    public static void postJson(String url, Headers header, Object json, String tag, RequestListener listener) {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        //json为String类型的json数据
        String jsonStr = JsonUtils.object2String(json);
        RequestBody requestBody = RequestBody.create(mediaType, jsonStr);
        //创建一个请求对象
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (header != null) {
            builder.headers(header);
        }
        Request request = builder.build();
        callRequest(url, request, tag, listener);
    }

    public static void callRequest(String url, Request request, String tag, RequestListener listener) {
        getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    String result = response.body().string();
                    if (listener != null) {
                        handler.post(() -> {
                            if (tag != null) {
                                listener.onSuccess(url, result, tag);
                            } else {
                                listener.onSuccess(url, result);
                            }
                        });

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (listener != null) {
                    Log.e(TAG + url, "error");
                    e.printStackTrace();
                    handler.post(() -> listener.onFailed(url, e));
                }
            }

        });
    }

}
