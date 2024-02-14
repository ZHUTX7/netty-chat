package com.mindset.ameeno;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author ztx
 * @date 2021-12-17 15:33
 * @description :
 * 应用场景：很多时候我们想要在某个类加载完毕时干某件事情，但是使用了spring管理对象，
 * 我们这个类引用了其他类（可能是更复杂的关联），所以当我们去使用这个类做事情时发现包空指针错误，
 * 这是因为ni b但是引用的其他类不一定初始化完成，所以发生了空指针错误，解决方案如下：
 * 1、写一个类继承spring的ApplicationListener监听，并监控ContextRefreshedEvent事件（容易初始化完成事件）
 *
 * 2、定义简单的bean：<bean id="beanDefineConfigue" class="com.creatar.portal.webservice.BeanDefineConfigue"></bean>
 *
 * 或者直接使用@Component("BeanDefineConfigue")注解方式
 */
@Component
public class NettyBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${netty-core-port}")
    private int port;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        if (event.getApplicationContext().getParent()==null) {
//            try {
//                WSServer.getInstance().start(port);
//                System.out.println("Netty 通讯器启动成功 ！ ");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
    }
}
