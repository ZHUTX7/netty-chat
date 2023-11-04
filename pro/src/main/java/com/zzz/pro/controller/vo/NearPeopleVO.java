package com.zzz.pro.controller.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhutianxiang
 * @Description 附近的人
 * @Date 2023/8/9 18:33
 * @Version 1.0
 */
@Data
public class NearPeopleVO {
    //附近的人列表 - 10个
    private List<NearUserVO> userList;
    //附近的人总数
    private int count;
    public NearPeopleVO(){
        this.count = 0 ;
        this.userList = new ArrayList<>();
    }
}
