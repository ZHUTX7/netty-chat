package com.proxy.common.util;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

/**
 * @author : ztx
 * @version :V1.0
 * @description :
 * @update : 2021/3/4 11:55
 */
public class SslContextFactory
{
    private static final String     PROTOCOL    = "TLS";    // TODO: which protocols will be adopted?
    private static final SSLContext SERVER_CONTEXT;
    private static final SSLContext CLIENT_CONTEXT;

    static
    {
        SSLContext serverContext = null;
        SSLContext clientContext = null;

        String keyStorePassword = "changeme";
        try
        {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(SslContextFactory.class.getClassLoader().getResourceAsStream("resource\\server-nonprod.jks"), keyStorePassword.toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyStorePassword.toCharArray());

            // truststore
            KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(SslContextFactory.class.getClassLoader().getResourceAsStream("resource\\cert\\server-nonprod.jks"),
                    keyStorePassword.toCharArray());

            // set up trust manager factory to use our trust store
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);

            // Initialize the SSLContext to work with our key managers.
            serverContext = SSLContext.getInstance(PROTOCOL);
            serverContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        } catch (Exception e)
        {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }

        try
        {
            // keystore
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(SslContextFactory.class.getClassLoader().getResourceAsStream("cert\\client.keystore"), keyStorePassword.toCharArray());

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, keyStorePassword.toCharArray());

            // truststore
            KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(SslContextFactory.class.getClassLoader().getResourceAsStream("cert\\clienttruststore.keystore"), keyStorePassword.toCharArray());

            // set up trust manager factory to use our trust store
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
            clientContext = SSLContext.getInstance(PROTOCOL);
            clientContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e)
        {
            throw new Error("Failed to initialize the client-side SSLContext", e);
        }

        SERVER_CONTEXT = serverContext;
        CLIENT_CONTEXT = clientContext;
    }

    public static SSLContext getServerContext()
    {
        return SERVER_CONTEXT;
    }

    public static SSLContext getClientContext()
    {
        return CLIENT_CONTEXT;
    }
}
