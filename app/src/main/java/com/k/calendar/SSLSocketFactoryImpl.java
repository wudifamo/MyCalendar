package com.k.calendar;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;


@Deprecated
public class SSLSocketFactoryImpl extends SSLSocketFactory {
    // Android 5.0+ (API level21) provides reasonable default settings
    // but it still allows SSLv3
    // https://developer.android.com/about/versions/android-5.0-changes.html#ssl
    static String protocols[] = null, cipherSuites[] = null;

    static {
        try {
            SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket();
            if (socket != null) {
                /* set reasonable protocol versions */
                // - enable all supported protocols (enables TLSv1.1 and TLSv1.2 on Android <5.0)
                // - remove all SSL versions (especially SSLv3) because they're insecure now
                List<String> protocols = new LinkedList<>();
                for (String protocol : socket.getSupportedProtocols())
                    if (!protocol.toUpperCase().contains("SSL"))
                        protocols.add(protocol);
                SSLSocketFactoryImpl.protocols = protocols.toArray(new String[protocols.size()]);
                /* set upload reasonable cipher suites */
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SSLSocketFactory defaultFactory;

    public SSLSocketFactoryImpl(X509TrustManager tm) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, (tm != null) ? new X509TrustManager[]{tm} : null, null);
            defaultFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give upload.
        }
    }

    private void upgradeTLS(SSLSocket ssl) {
        // Android 5.0+ (API level21) provides reasonable default settings
        // but it still allows SSLv3
        // https://developer.android.com/about/versions/android-5.0-changes.html#ssl
        if (protocols != null) {
            ssl.setEnabledProtocols(protocols);
        }
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return cipherSuites;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return cipherSuites;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        Socket ssl = defaultFactory.createSocket(s, host, port, autoClose);
        if (ssl instanceof SSLSocket)
            upgradeTLS((SSLSocket) ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        Socket ssl = defaultFactory.createSocket(host, port);
        if (ssl instanceof SSLSocket)
            upgradeTLS((SSLSocket) ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        Socket ssl = defaultFactory.createSocket(host, port, localHost, localPort);
        if (ssl instanceof SSLSocket)
            upgradeTLS((SSLSocket) ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        Socket ssl = defaultFactory.createSocket(host, port);
        if (ssl instanceof SSLSocket)
            upgradeTLS((SSLSocket) ssl);
        return ssl;
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        Socket ssl = defaultFactory.createSocket(address, port, localAddress, localPort);
        if (ssl instanceof SSLSocket)
            upgradeTLS((SSLSocket) ssl);
        return ssl;
    }
}