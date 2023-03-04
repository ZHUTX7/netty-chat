package com.zzz.pro.dao;

import com.zzz.pro.mapper.UserDatingMapper;
import com.zzz.pro.pojo.dto.UserDating;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class UserDatingRepository {
    @Resource
    private UserDatingMapper userDatingMapper;
    //接受匹配 Status+1 and set UserId
    public void updateDating(UserDating userDating){
        userDatingMapper.updateByPrimaryKey(userDating);

    }

    public void updateBothDatingStatus(String userId,String targetId,Integer iStatus,Integer uStatus){
        userDatingMapper.updateBothDatingStatus(userId,targetId,iStatus,uStatus);
    }

    public void updateDating( String userId,String targetId,Integer status){
        userDatingMapper.updateMyselfStatus(userId,targetId,status);
    }

    public Integer queryDatingStatus(String datingId){
        return userDatingMapper.queryDatingStatus(datingId);
    }
    public void delDatingData(String datingId){
        UserDating u = new UserDating();
        u.setDatingId(datingId);
        userDatingMapper.deleteByPrimaryKey(u);
    }

    public void addDatingData(UserDating userDating){
        userDating.setDatingId(userDating.getUserId()+userDating.getUserTargetId());
        userDatingMapper.insert(userDating);
    }
}
