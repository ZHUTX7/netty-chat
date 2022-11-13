package com.zzz.pro.service;

import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.pojo.dto.UserBaseInfo;
import com.zzz.pro.pojo.dto.UserMatch;
import com.zzz.pro.pojo.dto.UserPersonalInfo;
import com.zzz.pro.pojo.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.UserProfileVO;
import com.zzz.pro.utils.ResultVOUtil;
import io.netty.util.internal.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class FriendsServiceImpl implements FriendsService{

    @Resource
    UserRepository userRepository;

    @Resource
    RedisTemplate redisTemplate;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void match(String userId) {
        //确认该用户是否已经有建立匹配关系的对象
        UserBaseInfo user = new UserBaseInfo();
        user.setUserId(userId);
        if( ! ObjectUtils.isEmpty(userRepository.queryUserMatch(user))){
            throw new ApiException(500,"已经有匹配对象啦～ 好好聊吧");
        }
        UserPersonalInfo userPersonalInfo = userRepository.queryUserPerInfo(user.getUserId());
        UserProfileVO userProfileVO = new UserProfileVO();

        BeanUtils.copyProperties(userPersonalInfo,userProfileVO);
        int sex = userPersonalInfo.getUserSex();
        //进入Redis 匹配用户池
        if(sex==1){
            redisTemplate.opsForHash().put(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),user.getUserId(),userProfileVO);
        }else{
            redisTemplate.opsForHash().put(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),user.getUserId(),userProfileVO);
        }

    }

    @Override
    public void stopMatch(String userId) {
        redisTemplate.opsForHash().delete(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),userId);
        redisTemplate.opsForHash().delete(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),userId);
        log.info("用户ID{}停止匹配",userId);
    }


    //不在线的用户给不给他推荐？？
    @Override
    public List<UserProfileVO> pushMatchUserList(UserFilterForm userFilterForm) {
        int pushUserCount = 30;
        List<UserProfileVO> list = new ArrayList<>();
        if(userFilterForm.getSex()==1){
            Map<String,UserProfileVO> userMap = redisTemplate.opsForHash().entries(RedisKeyEnum.GIRLS_WAITING_POOL.getCode());
            //根据筛选条件计算
            for(Map.Entry<String,UserProfileVO> entry : userMap.entrySet()){
                list.add(entry.getValue());
            }

        }else{
            Map<String,UserProfileVO> userMap = redisTemplate.opsForHash().entries(RedisKeyEnum.BOYS_WAITING_POOL.getCode());
            //根据筛选条件计算
            for(Map.Entry<String,UserProfileVO> entry : userMap.entrySet()){
                list.add(entry.getValue());
            }
        }
        if(CollectionUtils.isEmpty(list)){
            //从离线用户中取
            int findUserSex ;
            UserPersonalInfo u = new UserPersonalInfo();
            if(userFilterForm.getSex()==1){
                findUserSex = 0;
            }else {
                findUserSex = 1;
            }

            u.setUserSex(findUserSex);
            List<UserPersonalInfo> allUser =  userRepository.getAll(u);

            BeanUtils.copyProperties(allUser,list);
        }
        return list;
    }

    //解除匹配
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delMatch(UserMatch userMatch) {
        try{
            String a = userMatch.getMyUserId();
            String b = userMatch.getMatchUserId();
            userRepository.delMatchUsers(userMatch);
            userMatch.setMyUserId(b);
            userMatch.setMatchUserId(a);
            userRepository.delMatchUsers(userMatch);

        }catch (Exception e){
            throw new ApiException(500,"解除失败～");
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserPersonalInfo getMatchPerson(UserBaseInfo userBaseInfo) {

        return userRepository.getMatchPerson(userBaseInfo.getUserId());

    }

    @Override
    public SysJSONResult boostMatch(String userId, String targetId) {
        //1.判断是否为单向选择
        String me = (String)redisTemplate.opsForValue().get(RedisKeyEnum.MATCH_SELECTED_POOL.getCode()+targetId);
        if(StringUtils.isEmpty(me)){
            redisTemplate.opsForValue().set(RedisKeyEnum.MATCH_SELECTED_POOL.getCode()+userId,targetId);

           return ResultVOUtil.error(201,null);
        }
        else {
            UserMatch userMatch = new UserMatch();
            userMatch.setMyUserId(userId);
            userMatch.setMatchUserId(targetId);
            userRepository.addMatchUsers(userMatch);
            return ResultVOUtil.success("恭喜，匹配成功～",null);
        }
    }


}
