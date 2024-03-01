package com.mindset.ameeno.service;

import com.mindset.ameeno.enums.UserRoleEnum;
import com.mindset.ameeno.mapper.UserRoleMapper;
import com.mindset.ameeno.pojo.dto.UserRole;
import com.mindset.ameeno.utils.CRCUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Author zhutianxiang
 * @Description TODO
 * @Date 2024/2/17 14:18
 * @Version 1.0
 */
@Service
public class UserRoleService {
    @Resource
    UserRoleMapper userRoleMapper;

    public void userRoleInit(String userId){
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setId(CRCUtil.crc32Hex(UUID.randomUUID().toString()));
        userRole.setRoleType(UserRoleEnum.NORMAL_ROLE.getCode());
        userRoleMapper.insert(userRole);
    }
}
