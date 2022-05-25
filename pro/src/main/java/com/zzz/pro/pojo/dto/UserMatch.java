package com.zzz.pro.pojo.dto;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "user_match")
public class UserMatch {
    /**
     * 序列号
     */
    @Id
    private int id;

    @Column(name = "my_user_id")
    private String myUserId;

    @Column(name = "match_user_id")
    private String matchUserId;

    /**
     * 活跃状态
     */
    @Column(name = "active_state")
    private Integer activeState;

    /**
     * 获取序列号
     *
     * @return id - 序列号
     */
    public int getId() {
        return id;
    }

    /**
     * 设置序列号
     *
     * @param id 序列号
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return my_user_id
     */
    public String getMyUserId() {
        return myUserId;
    }

    /**
     * @param myUserId
     */
    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }

    /**
     * @return match_user_id
     */
    public String getMatchUserId() {
        return matchUserId;
    }

    /**
     * @param matchUserId
     */
    public void setMatchUserId(String matchUserId) {
        this.matchUserId = matchUserId;
    }

    /**
     * 获取活跃状态
     *
     * @return active_state - 活跃状态
     */
    public Integer getActiveState() {
        return activeState;
    }

    /**
     * 设置活跃状态
     *
     * @param activeState 活跃状态
     */
    public void setActiveState(Integer activeState) {
        this.activeState = activeState;
    }
}