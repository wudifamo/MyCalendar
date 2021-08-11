package com.k.calendar;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * User: ljx
 * Date: 2019/1/21
 * Time: 14:11
 */
@Deprecated
public class X509TrustManagerImpl implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
