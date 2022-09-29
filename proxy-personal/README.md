# 简介

Proxy-master 是一个内网穿透程序。

更新（2021/8/26）：

目前支持HTTP,UDP,TCP的流量转发。

添加了Redis，将网关的路由信息存储在Redis层，并逐渐替换掉原有的程序内存存储路由信息方案。使之可以动态修改路由配置信息（待改进)

在TCP通信上添加了SM4国密算法加解密

添加了TLS协议

添加了用户端访问拦截器

集成了SPA单包认证

动态端口分配



## 工作流程

![image-20210826104145644](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20210826104145644.png)




