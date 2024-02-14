package com.mindset.ameeno.service;

import com.mindset.ameeno.enums.RedisKeyEnum;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.mapper.UserFriendsMapper;
import com.mindset.ameeno.mapper.UserMatchMapper;
import com.mindset.ameeno.mapper.UserPersonalInfoMapper;
import com.mindset.ameeno.pojo.dto.UserFriends;
import com.mindset.ameeno.controller.vo.FriendsVO;
import com.mindset.ameeno.controller.vo.UserVO;
import com.mindset.ameeno.utils.CRCUtil;
import com.mindset.ameeno.utils.RedisStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author zhutianxiang
 * @Description 好友列表
 * @Date 2023/8/3 00:17
 * @Version 1.0
 */
@Slf4j
@Service
public class FriendsService {
    @Resource
    UserPersonalInfoMapper userPersonalInfoMapper;

    @Resource
    UserMatchMapper userMatchMapper;
    @Resource
    RedisStringUtil redisStringUtil;
    @Resource
    UserFriendsMapper userFriendsMapper;

    @Transactional
    public void makeFriendsRel(String userId, String targetId) {
        UserFriends userFriends = new UserFriends();
        userFriends.setId(CRCUtil.crc32Hex(targetId + userId));
        userFriends.setUserId(userId);
        userFriends.setFriendsId(targetId);
        userFriends.setFriendsStatus(1);
        userFriends.setCreatTime(new Date());
        userFriendsMapper.insert(userFriends);

        userFriends.setId(CRCUtil.crc32Hex(userId + targetId));
        userFriends.setUserId(targetId);
        userFriends.setFriendsId(userId);
        userFriendsMapper.insert(userFriends);
        userMatchMapper.deleteRelByUid(userId,targetId);
    }

    public List<FriendsVO> queryFriendsList(String userId) {
        List<FriendsVO> friendsIds = userFriendsMapper.selectFriendsList(userId);
        if (CollectionUtils.isEmpty(friendsIds)) {
            return null;
        }
        return friendsIds;

    }


    public void removeFriendsRel(String userId, String targetId) {
        userFriendsMapper.updateFriendsStatus(userId, targetId, 4);
        int status = userFriendsMapper.queryFriendsRelStatus(targetId, userId);
        if (4 != status) {
            userFriendsMapper.updateFriendsStatus(targetId, userId, 3);
        }


    }


    public FriendsVO getFriendsVO(String userId, String targetId) {
        //TODO modify

        UserVO userVO = (UserVO) redisStringUtil.hget(RedisKeyEnum.ALL_USER_VO.getCode(), targetId);
        if (userVO == null) {
            userVO = userPersonalInfoMapper.queryUserVO(targetId);
            if (userVO == null) {
                throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "该好友账户已经注销");
            }
        }
        FriendsVO friendsVO = new FriendsVO();
        friendsVO.setUserImage(userVO.getUserImage());
        friendsVO.setUserId(targetId);
        friendsVO.setUserNickName(userVO.getUserNickName());
        friendsVO.setRealAuth(userVO.getRealAuth());
        // TODO
        int status = userFriendsMapper.queryFriendsRelStatus(userId, targetId);
        friendsVO.setFriendsStatus(status);
        return friendsVO;
    }
}
