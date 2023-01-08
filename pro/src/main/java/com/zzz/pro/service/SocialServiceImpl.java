package com.zzz.pro.service;

import com.zzz.pro.dao.UserDatingRepository;
import com.zzz.pro.dao.UserFriendsRepo;
import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.pojo.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.DatingStatusVO;
import com.zzz.pro.pojo.vo.FriendsVO;
import com.zzz.pro.pojo.vo.UserProfileVO;
import com.zzz.pro.utils.ResultVOUtil;
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
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SocialServiceImpl implements SocialService{

    @Resource
    UserRepository userRepository;

    @Resource
    UserDatingRepository userDatingRepository;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    UserFriendsRepo userFriendsRepo;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void match(String userId) {
        //确认该用户是否已经有建立匹配关系的对象
        UserBaseInfo user = new UserBaseInfo();
        user.setUserId(userId);

        if(redisTemplate.opsForHash().get(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),user.getUserId())!=null
                || redisTemplate.opsForHash().get(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),user.getUserId())!=null ){
            return;
        }

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
    public List<UserProfileVO> pushMatchUserList(UserFilterForm userFilterForm,String userId) {
        int pushUserCount = 30;
        List<UserProfileVO> list = new ArrayList<>();
        if(userFilterForm.getSex()==1){
            Map<String,UserProfileVO> userMap = redisTemplate.opsForHash().entries(RedisKeyEnum.GIRLS_WAITING_POOL.getCode());
            //根据筛选条件计算
            for(Map.Entry<String,UserProfileVO> entry : userMap.entrySet()){
                //TODO 筛选测试阶段关闭
//                String userId = entry.getValue().getUserId();
//                boolean isDislikeUser = redisTemplate.opsForHash().hasKey(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+"userId",userId);
//                if(isDislikeUser){
//                    continue;
//                }
                list.add(entry.getValue());
            }
            //如果推送数量不够，拿离线用户补
            if(list.size()<30){
               List<UserPersonalInfo> u=   userRepository.queryUnMatchUserList(userId).stream().filter(
                       e ->  (!userMap.containsKey( e.getUserId())
               )).collect(Collectors.toList());

               u.stream().forEach(e->{
                   UserProfileVO vo = new UserProfileVO();
                   BeanUtils.copyProperties(e,vo);
                   list.add(vo);
               });
            }
            return list;


        }else{
            Map<String,UserProfileVO> userMap = redisTemplate.opsForHash().entries(RedisKeyEnum.BOYS_WAITING_POOL.getCode());
            //根据筛选条件计算
            for(Map.Entry<String,UserProfileVO> entry : userMap.entrySet()){
                //TODO 筛选测试阶段关闭
//                String userId = entry.getValue().getUserId();
//                boolean isDislikeUser = redisTemplate.opsForHash().hasKey(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+"userId",userId);
//                if(isDislikeUser){
//                    continue;
//                }
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

            List<UserPersonalInfo> allUser =  userRepository.getAllByExample(u);

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
    public void delBlackUserList(String userId) {
        redisTemplate.delete(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+userId);
    }


    @Override
    public void addBlackUserList(String userId, List<String> targetUserIds){
        redisTemplate.opsForSet().add(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+userId,targetUserIds);
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

    @Override
    public void unBoostMatch(String userId, String targetId) {
        String me = (String)redisTemplate.opsForValue().get(RedisKeyEnum.MATCH_SELECTED_POOL.getCode()+targetId);
        redisTemplate.opsForHash().put(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+"userId",targetId,1);
    }



    @Override
    public void acceptDating(String userId, String targetId) {
        String datingId = userId + targetId;
        datingId ="dat"+  datingId.hashCode();
        UserDating example = new UserDating();
        example.setUserId(userId);
        example.setUserTargetId(targetId);
        example.setDatingId(datingId);
        // 查询约会状态
        UserDating dating = userDatingRepository.queryDating(example);
        if(ObjectUtils.isEmpty(dating)){
            example.setDatingId(datingId);
            return;
        }
        String status = dating.getStatus();

        // 1. 对方已经同意
        if(status.equals(userId)){
            dating.setStatus("both");
            userDatingRepository.updateDating(dating);
        }
        // 2. 仅自己同意 - return
        // 3. 双方全部同意
        else {
            return;
        }
    }

    @Override
    public void completeDating(String userId, String targetId) {
        String datingId = userId + targetId;
        datingId ="dat"+  datingId.hashCode();
        //删除约会数据
        userDatingRepository.delDatingData(datingId);
        //添加到好友列表
        makeFriendsRel(userId,targetId);
    }

    @Override
    public DatingStatusVO queryDatingStatus(String userId, String targetId) {
        String datingId = userId + targetId;
        datingId ="dat"+  datingId.hashCode();
        UserDating example = new UserDating();
        example.setUserId(userId);
        example.setUserTargetId(targetId);
        example.setDatingId(datingId);
        // 查询约会状态
        UserDating dating = userDatingRepository.queryDating(example);
        if(ObjectUtils.isEmpty(dating)){
            throw new ApiException(401,"数据错误，两人之间并无匹配");
        }
        DatingStatusVO vo = new DatingStatusVO();

        if(dating.getStatus().equals(targetId)){
            vo.setStatus(2);
            vo.setMsg("对方提出约会邀请～");
        }else if(dating.getStatus().equals(userId)){
            vo.setStatus(1);
            vo.setMsg("等待对方接受邀请～");
        }else {
            vo.setStatus(0);
            vo.setMsg("快发起约会邀请～");
        }
        return vo;

    }

    @Override
    public void makeFriendsRel(String userId, String targetId) {
        UserFriends userFriends = new UserFriends();
        userFriends.setUserId(userId);
        userFriends.setFriendsId(targetId);
        userFriends.setFriendsStatus(2);
        userFriends.setCreatTime(new Timestamp(new Date().getTime()));
        userFriendsRepo.addFriends(userFriends);
    }

    @Override
    public List<FriendsVO> queryFriendsList(String userId) {
      List<String> friendsIds =  userFriendsRepo.queryFriendsId(userId);
      if(CollectionUtils.isEmpty(friendsIds)){
          return null;
      }
      List<Map >  list = userRepository.queryUnMatchUserList(friendsIds);

      if(CollectionUtils.isEmpty(list)){
        return null;
      }

      return list.stream().map(e->{
        FriendsVO vo = new FriendsVO();
        vo.setUserId((String)e.get("user_id"));
        vo.setUserImage((String)e.get("user_nickname"));
        vo.setUserNickName((String)e.get("user_face_image"));
        return vo;}).collect(Collectors.toList());

    }

    @Override
    public void removeFriendsRel(String userId, String targetId) {

    }

    @Override
    public void delFriendsData(String userId, String targetId) {

    }

}
