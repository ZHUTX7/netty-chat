package com.proxy.client.enity;



import java.sql.Timestamp;

/**
 * @author : ztx
 * @version :V1.0
 * @description : 用户实体类
 * @update : 2021/2/23 11:32
 */
public class User {
    private int user_id;
    private String user_name;
    private String nickname;
    private String password;
    private String ip_address;
    private String mac_address;
    private int login_state;
    private Timestamp last_login_time;//上次登录时间
    private String phone;
    private String email;
    private int user_level; //用户访问权限等级

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public int getLogin_state() {
        return login_state;
    }

    public void setLogin_state(int login_state) {
        this.login_state = login_state;
    }

    public Timestamp getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Timestamp last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUser_level() {
        return user_level;
    }

    public void setUser_level(int user_level) {
        this.user_level = user_level;
    }
}
