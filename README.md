# netty-chat

聊天交友软件后台系统Demo

目前的功能:

1.用户模块：注册登录HTTP接口 

2.通信模块：用户上线、发送信息接受信息接口



环境准备：

1.Java 8/11

2.mysql

3.kafka





# 聊天通信格式

## 1.用户上线

```
请求地址: ws://ip:9999/ws
```

```
{
    "action":1,
    "chatMsg":{
        "senderId":"10001"
    },
    "token":"123"
}
```

## 2.用户发送信息

```
请求地址: ws://ip:9999/ws
```

```
{
    "action": 2,
    "chatMsg": {
        "senderId": "10001",
        "receiverId": "10002",
        "msg": "hello, i'm 10001 ",
        "msgId": "12",
        "msgType": 1,
        "sendTime": "1651995740"
    },
    "expand": null,
    "token": "123"
}
```

其中：

​        "expand": 扩展位，目前业务用不到

​        "msgId" 为空
​        "msgType":  手机端发送时赋值
​        "sendTime": 手机端发送时赋值（时间戳）

```
msgType int 类型，对应关系如下
//    1 -  'TEXT'
//     2 -        'IMAGE'
//     3 -       'VIDEO'
//     4 -      'VOICE'
```



## PS：

A 用户向 B用户发送信息时，"action" = 2

B用户接到信息时，JSOn中"action" = 3 ，方便判断用户是接收方还是发送方。

```
action对应关系：
1 - 用户上线
2 - 聊天.发送消息
3 - 聊天.收到消息
```



# MYSQL

```
/*
 Navicat Premium Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 80029
 Source Host           : localhost:3306
 Source Schema         : chat

 Target Server Type    : MySQL
 Target Server Version : 80029
 File Encoding         : 65001

 Date: 26/05/2022 04:37:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_msg
-- ----------------------------
DROP TABLE IF EXISTS `chat_msg`;
CREATE TABLE `chat_msg` (
  `msg_id` bigint NOT NULL COMMENT '消息ID',
  `send_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发送人',
  `accept_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收件人',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息',
  `sign_flag` int NOT NULL COMMENT '消息是否签收',
  `send_time` datetime NOT NULL COMMENT '发送时间',
  `message_type` int DEFAULT NULL COMMENT '消息类型',
  PRIMARY KEY (`msg_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='聊天信息表';

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int NOT NULL,
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sum` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for user_base_info
-- ----------------------------
DROP TABLE IF EXISTS `user_base_info`;
CREATE TABLE `user_base_info` (
  `user_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `user_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `user_password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `user_login_state` int DEFAULT NULL COMMENT '登录状态',
  `user_role` int DEFAULT NULL COMMENT '用户角色：普通用户、VIP。。。',
  `last_login_time` timestamp NULL DEFAULT NULL COMMENT '上次登录时间',
  `user_coordination` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户登录地址',
  PRIMARY KEY (`user_id`) USING BTREE,
  KEY `id` (`user_id`) USING BTREE COMMENT 'iddd'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户基础信息';

-- ----------------------------
-- Table structure for user_match
-- ----------------------------
DROP TABLE IF EXISTS `user_match`;
CREATE TABLE `user_match` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '序列号',
  `my_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `match_user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `active_state` int DEFAULT NULL COMMENT '活跃状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='匹配表';

-- ----------------------------
-- Table structure for user_personal_info
-- ----------------------------
DROP TABLE IF EXISTS `user_personal_info`;
CREATE TABLE `user_personal_info` (
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户ID',
  `user_nickname` varchar(27) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户昵称',
  `user_gender` int DEFAULT NULL COMMENT '年龄',
  `user_motto` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '个性签名',
  `user_memo` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `user_education` int DEFAULT NULL COMMENT '学历',
  `user_profession` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职业',
  `user_constellation` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '星座（不加具体出生年月日）',
  `user_location` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '当前位置',
  `user_hometown` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '家乡',
  `user_height` int DEFAULT NULL COMMENT '身高',
  `user_weight` int DEFAULT NULL COMMENT '体重',
  `user_face_image` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '用户头像',
  `user_face_image_big` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '用户高清头像',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户个人信息';

SET FOREIGN_KEY_CHECKS = 1;

```







