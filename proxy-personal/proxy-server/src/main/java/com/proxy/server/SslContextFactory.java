package com.proxy.server;

import cn.hutool.core.io.resource.ClassPathResource;
import com.proxy.server.service.ConfigService;
import com.proxy.server.service.ServerBeanManager;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

/**
 * @author : admin
 * @version :V1.0
 * @description :
 * @update : 2021/3/4 11:55
 */
public class SslContextFactory
{
    private static final SSLContext SSL_CONTEXT_S ;
    private static  String CLIENT_JKS_PATH;
    static {
        ServerBeanManager.getConfigService().readServerConfig();

        ConfigService configService = ServerBeanManager.getConfigService();
        CLIENT_JKS_PATH = (String)configService.getConfigure("serverJksPath");
    }
    static{
        SSLContext sslContext = null ;

        try {
            sslContext = SSLContext.getInstance("TLSv1") ;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        try{
            if(getKeyManagersServer() != null && getTrustManagersServer() != null ){
                sslContext.init(getKeyManagersServer(), getTrustManagersServer(), null);
            }
        }catch(Exception e){
            e.printStackTrace() ;
        }
        sslContext.createSSLEngine().getSupportedCipherSuites() ;

        SSL_CONTEXT_S = sslContext ;
    }
    public SslContextFactory(){

    }
    private static TrustManager[] getTrustManagersServer(){
        FileInputStream is = null ;
        KeyStore ks = null ;
        TrustManagerFactory keyFac = null ;

        TrustManager[] kms = null ;
        try {
            // 获得KeyManagerFactory对象. 初始化位默认算法
            keyFac = TrustManagerFactory.getInstance("SunX509") ;
            is =new FileInputStream(new File(CLIENT_JKS_PATH));
            ks = KeyStore.getInstance("JKS") ;
            String keyStorePass = "cahy1234" ;
            ks.load(is, keyStorePass.toCharArray()) ;
            keyFac.init(ks) ;
            kms = keyFac.getTrustManagers() ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(is != null ){
                try {
                    is.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return kms ;
    }

    private static KeyManager[] getKeyManagersServer(){
        FileInputStream is = null ;
        KeyStore ks = null ;
        KeyManagerFactory keyFac = null ;

        KeyManager[] kms = null ;
        try {
            // 获得KeyManagerFactory对象. 初始化位默认算法
            keyFac = KeyManagerFactory.getInstance("SunX509") ;
            is =new FileInputStream(new File(CLIENT_JKS_PATH) );
            ks = KeyStore.getInstance("JKS") ;
            String keyStorePass = "cahy1234" ;
            ks.load(is , keyStorePass.toCharArray()) ;
            keyFac.init(ks, keyStorePass.toCharArray()) ;
            kms = keyFac.getKeyManagers() ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(is != null ){
                try {
                    is.close() ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return kms ;
    }


    public static SSLContext getServerContext()
    {
        return SSL_CONTEXT_S;
    }

}
