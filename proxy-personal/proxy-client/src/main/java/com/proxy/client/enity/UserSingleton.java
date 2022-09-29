package com.proxy.client.enity;

/**
 * @author : ztx
 * @version :V1.0
 * @description :
 * @update : 2021/2/23 11:35
 */
public class UserSingleton {
    private static User user = null;

    public static User getUser(){
        //此处直接返回就行
//        if(user == null){
//            user =  new User();
//        }
        return user;
    }

    public static void setUser(User u){
        user = u;
    }
}
