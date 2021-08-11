package com.k.calendar;

import java.io.IOException;

public interface RequestListener {

    void onSuccess(String url, String result);

    void onSuccess(String url, String result, String tag);

    /**
     * 下载进度回调
     *
     * @param progress 进度
     */
    void onProgress(int progress);

    void onFailed(String url, IOException e);
}
