package com.zzz.pro.pojo.bo;

import com.zzz.pro.controller.vo.UserProfileVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PushUserListBO  implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer action;
    private List<UserProfileVO> msg;

}
