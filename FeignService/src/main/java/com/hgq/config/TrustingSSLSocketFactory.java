package com.hgq.config;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * todo
 *
 * @Author hgq
 * @Date: 2022-07-25 15:27
 * @since 1.0
 **/
public class TrustingSSLSocketFactory  extends SSLSocketFactory
        implements X509TrustManager, X509KeyManager {
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    private static final Map<String, SSLSocketFactory> sslSocketFactories =
            new LinkedHashMap<String, SSLSocketFactory>();
    private static final char[] KEYSTORE_PASSWORD = "rabbit".toCharArray();
    private final static String[] ENABLED_CIPHER_SUITES = {"TLS_RSA_WITH_AES_256_CBC_SHA"};
    private final SSLSocketFactory delegate;
    private final String serverAlias;
    private final PrivateKey privateKey;
    private final X509Certificate[] certificateChain;

    private TrustingSSLSocketFactory(String serverAlias) {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(new KeyManager[] {this}, new TrustManager[] {this}, new SecureRandom());
            this.delegate = sc.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.serverAlias = serverAlias;
        if (serverAlias.isEmpty()) {
            this.privateKey = null;
            this.certificateChain = null;
        } else {
            try {
                KeyStore keyStore =
                        loadKeyStore(TrustingSSLSocketFactory.class.getResourceAsStream("/rabbittruststore.jks"));
                this.privateKey = (PrivateKey) keyStore.getKey(serverAlias, KEYSTORE_PASSWORD);
                java.security.cert.Certificate[] rawChain = keyStore.getCertificateChain(serverAlias);
                this.certificateChain = Arrays.copyOf(rawChain, rawChain.length, X509Certificate[].class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static SSLSocketFactory get() {
        return get("");
    }

    public synchronized static SSLSocketFactory get(String serverAlias) {
        if (!sslSocketFactories.containsKey(serverAlias)) {
            sslSocketFactories.put(serverAlias, new TrustingSSLSocketFactory(serverAlias));
        }
        return sslSocketFactories.get(serverAlias);
    }

    static Socket setEnabledCipherSuites(Socket socket) {
        SSLSocket.class.cast(socket).setEnabledCipherSuites(ENABLED_CIPHER_SUITES);
        return socket;
    }

    private static KeyStore loadKeyStore(InputStream inputStream) throws IOException {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, KEYSTORE_PASSWORD);
            return keyStore;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            inputStream.close();
        }
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return ENABLED_CIPHER_SUITES;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return ENABLED_CIPHER_SUITES;
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose)
            throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
            throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
            throws IOException {
        return setEnabledCipherSuites(delegate.createSocket(address, port, localAddress, localPort));
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        return null;
    }

    @Override
    public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
        return null;
    }

    @Override
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        return null;
    }

    @Override
    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        return serverAlias;
    }

    @Override
    public X509Certificate[] getCertificateChain(String s) {
        return certificateChain;
    }

    @Override
    public PrivateKey getPrivateKey(String alias) {
        return privateKey;
    }
}
