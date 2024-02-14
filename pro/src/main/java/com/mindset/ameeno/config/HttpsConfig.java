//package com.zzz.pro.config;
//import org.apache.catalina.Context;
//import org.apache.catalina.connector.Connector;
//import org.apache.tomcat.util.descriptor.web.SecurityCollection;
//import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @Author zhutianxiang
// * @Description https配置，将http请求全部转发到https
// * @Date 2023/10/10 19:52
// * @Version 1.0
// */
//@Configuration
//public class HttpsConfig {
//
//    @Value("${custom.http-port: 8857}")
//    private Integer httpPort;
//
//    @Value("${server.port}")
//    private Integer port;
//
////    @Bean
////    public TomcatServletWebServerFactory servletContainer() {
////        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
////        tomcat.addAdditionalTomcatConnectors(httpConnector());
////        return tomcat;
////    }
////
////    @Bean
////    public Connector httpConnector() {
////        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
////        connector.setPort(httpPort);
////        return connector;
////    }
//
//    @Bean
//    public TomcatServletWebServerFactory servletContainer() {
//        // 将http请求转换为https请求
//        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//            @Override
//            protected void postProcessContext(Context context) {
//                SecurityConstraint constraint = new SecurityConstraint();
//                // 默认为NONE
//                constraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                // 所有的东西都https
//                collection.addPattern("/*");
//                constraint.addCollection(collection);
//                context.addConstraint(constraint);
//
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(httpConnector());
//        return tomcat;
//    }
//
//    /**
//     * 强制将所有的http请求转发到https
//     *
//     * @return httpConnector
//     */
//    @Bean
//    public Connector httpConnector() {
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        // connector监听的http端口号
//        connector.setPort(httpPort);
//        connector.setSecure(false);
//        // 监听到http的端口号后转向到的https的端口号
//        connector.setRedirectPort(port);
//        return connector;
//    }
//}
//
