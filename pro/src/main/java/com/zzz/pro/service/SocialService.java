package com.zzz.pro.service;

import com.zzz.pro.dao.UserDatingRepository;
import com.zzz.pro.dao.UserFriendsRepo;
import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.enums.SexEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.pojo.form.UserFilterForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.DatingStatusVO;
import com.zzz.pro.pojo.vo.FriendsVO;
import com.zzz.pro.pojo.vo.UserProfileVO;
import com.zzz.pro.utils.CRCUtil;
import com.zzz.pro.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
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
public class SocialService{

    @Resource
    UserRepository userRepository;

    @Resource
    UserDatingRepository userDatingRepository;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    UserFriendsRepo userFriendsRepo;


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
            throw new ApiException(200,"已经有匹配对象啦～ 好好聊吧");
        }


        UserPersonalInfo userPersonalInfo = userRepository.queryUserPerInfo(user.getUserId());
        UserProfileVO userProfileVO = new UserProfileVO();

        BeanUtils.copyProperties(userPersonalInfo,userProfileVO);
        String sex = userPersonalInfo.getUserSex();
        //进入Redis 匹配用户池
        if(sex.equals(SexEnum.MALE.getCode())){
            redisTemplate.opsForHash().put(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),user.getUserId(),userProfileVO);
        }else if(sex.equals(SexEnum.FEMALE.getCode())){
            redisTemplate.opsForHash().put(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),user.getUserId(),userProfileVO);
        }

    }


    public void stopMatch(String userId) {
        redisTemplate.opsForHash().delete(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),userId);
        redisTemplate.opsForHash().delete(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),userId);
        log.info("用户ID{}停止匹配",userId);
    }


    public Map<String,String> queryMatchStatus(String userId) {
        Map<String,String> map = new HashMap<>();
       if(redisTemplate.opsForHash().hasKey(RedisKeyEnum.BOYS_WAITING_POOL.getCode(),userId)
         ||redisTemplate.opsForHash().hasKey(RedisKeyEnum.GIRLS_WAITING_POOL.getCode(),userId) ) {
            map.put("matchStatus","on");
       }
       else{
           map.put("matchStatus","off");
       }
       return map;
    }



    public List<UserProfileVO> pushMatchUserList(UserFilterForm userFilterForm,String userId) {
        int pushUserCount = 30;
        List<UserProfileVO> list = new ArrayList<>();
        if(userFilterForm.getSex().equals(SexEnum.MALE.getCode())){
            Map<String,UserProfileVO> userMap = redisTemplate.opsForHash().entries(RedisKeyEnum.GIRLS_WAITING_POOL.getCode());
            //根据筛选条件计算
            for(Map.Entry<String,UserProfileVO> entry : userMap.entrySet()){
                //TODO 筛选测试阶段关闭
                if(userId .equals(entry.getValue().getUserId()) ){
                    continue;
                }
//                boolean isDislikeUser = redisTemplate.opsForHash().hasKey(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+"userId",userId);
//                if(isDislikeUser){
//                    continue;
//                }
//
                list.add(entry.getValue());
            }
            //如果推送数量不够，拿离线用户补
            if(list.size()<30){
               List<UserPersonalInfo> u=   userRepository.queryUnMatchUserList(userId).stream().filter(
                       e ->  (!userMap.containsKey( e.getUserId())
               )).collect(Collectors.toList());

               u.stream().forEach(e->{
                   UserProfileVO vo = new UserProfileVO();
                   if(!userId .equals(e.getUserId()) ){
                       BeanUtils.copyProperties(e,vo);
                       list.add(vo);
                   }
               });
            }
            return list;


        }else{
            Map<String,UserProfileVO> userMap = redisTemplate.opsForHash().entries(RedisKeyEnum.BOYS_WAITING_POOL.getCode());
            //根据筛选条件计算
            for(Map.Entry<String,UserProfileVO> entry : userMap.entrySet()){
                //TODO 筛选测试阶段关闭
                if(userId == entry.getValue().getUserId()){
                    continue;
                }
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
            if(userFilterForm.getSex().equals(SexEnum.FEMALE.getCode())){
                findUserSex = 0;
            }else {
                findUserSex = 1;
            }

            u.setUserSex(userFilterForm.getSex());

            List<UserPersonalInfo> allUser =  userRepository.getAllByExample(u);
            for(UserPersonalInfo e:allUser){
                if(e.getUserId().equals(userId)){
                    continue;
                }
                UserProfileVO vo = new UserProfileVO();
                BeanUtils.copyProperties(e,vo);
                list.add(vo);
            }

        }
        return list;
    }

    //解除匹配
    @Transactional(propagation = Propagation.REQUIRED)
    public void delMatch(UserMatch userMatch) {
        try{
            String a = userMatch.getMyUserId();
            String b = userMatch.getMatchUserId();
            userRepository.delMatchUsers(userMatch);
            userMatch.setMyUserId(b);
            userMatch.setMatchUserId(a);
            userRepository.delMatchUsers(userMatch);
            //TODO 删除dating数据 添加到不喜欢列表
//            userDatingRepository.delDatingData();

        }catch (Exception e){
            throw new ApiException(500,"解除失败～");
        }

    }

    public void delBlackUserList(String userId) {
        redisTemplate.delete(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+userId);
    }



    public void addBlackUserList(String userId, List<String> targetUserIds){
        redisTemplate.opsForSet().add(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+userId,targetUserIds);
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public UserPersonalInfo getMatchPerson(String userId) {
        return userRepository.getMatchPerson(userId);
    }


    public SysJSONResult boostMatch(String userId, String targetId) {
        //1.判断是否为单向选择(我是否被他喜欢
        String crc = CRCUtil.crc32Hex(targetId+userId);
        String me = (String)redisTemplate.opsForValue().get(RedisKeyEnum.MATCH_SELECTED_POOL.getCode()+crc);
        if(StringUtils.isEmpty(me)){
            //说明对方没有选过我
            crc = CRCUtil.crc32Hex(userId+targetId);
            redisTemplate.opsForValue().set(RedisKeyEnum.MATCH_SELECTED_POOL.getCode()+crc,"1");
            return ResultVOUtil.error(201,null);
        }
        else {
            //TODO
            UserMatch userMatch = new UserMatch();
            userMatch.setMyUserId(userId);
            userMatch.setMatchUserId(targetId);
            userMatch.setActiveState(1);
            UserMatch userMatch2 = new UserMatch();
            userMatch2.setMyUserId(targetId);
            userMatch2.setMatchUserId(userId);
            userMatch2.setActiveState(1);

            userRepository.addMatchUsers(userMatch);
            userRepository.addMatchUsers(userMatch2);
            redisTemplate.delete(RedisKeyEnum.MATCH_SELECTED_POOL.getCode()+crc);
//            String datingId;
//            if(userId.hashCode()>targetId.hashCode()){
//                datingId = CRCUtil.crc32Hex(userId+targetId);
//            }else {
//                datingId = CRCUtil.crc32Hex(targetId+userId);
//            }
            UserDating dating = new UserDating();
            dating.setUserId(userId);
            dating.setUserTargetId(targetId);
            dating.setStatus(0);
            UserDating dating2 = new UserDating();
            dating2.setUserId(targetId);
            dating2.setUserTargetId(userId);
            dating2.setStatus(0);
            userDatingRepository.addDatingData(dating);
            userDatingRepository.addDatingData(dating2);
            return ResultVOUtil.success("恭喜，匹配成功～",null);
        }
    }


    public void unBoostMatch(String userId, String targetId) {
        redisTemplate.opsForHash().put(RedisKeyEnum.DISLIKE_USER_POOL.getCode()+"userId",targetId,1);
    }




    public void acceptDating(String userId, String targetId,Integer stautus) {

        UserDating example = new UserDating();
        example.setUserId(targetId);
        example.setUserTargetId(userId);
        // 查询约会状态
        Integer targetStatus  = userDatingRepository.queryDatingStatus(targetId+userId);
        if(ObjectUtils.isEmpty(targetStatus)){
            return;
        }

        //我到了约会地点
        if(stautus==5){
            //对方也到目标地点
            if(targetStatus==5){
                completeDating(userId,targetId);
                return;
            }
            userDatingRepository.updateBothDatingStatus(userId,targetId,5,6);
            return;
        }
        // 1. 对方已经同意见面
        if(targetStatus.equals(1)){
            //更新自己dating状态
            userDatingRepository.updateBothDatingStatus(userId,targetId,3,3);
        }else{
            //对方没同意
            //对方的约会状态改为2 自己的改为1
            userDatingRepository.updateBothDatingStatus(userId,targetId,1,2);
        }

    }


    public void completeDating(String userId, String targetId) {
        userDatingRepository.updateBothDatingStatus(userId,targetId,4,4);

        //删除约会数据
//        userDatingRepository.delDatingData(datingId);
        //添加到好友列表
        makeFriendsRel(userId,targetId);
    }


    public DatingStatusVO queryDatingStatus(String userId, String targetId) {
        // 查询约会状态
        Integer status =  userDatingRepository.queryDatingStatus(userId+targetId);
        DatingStatusVO vo = new DatingStatusVO();

        if(status==2){
            vo.setStatus(2);
            vo.setMsg("对方提出约会邀请～");
        }else if(status==1){
            vo.setStatus(1);
            vo.setMsg("等待对方接受邀请～");
        }else if(status==4){
            vo.setStatus(4);
            vo.setMsg("约会完成～");
        }else if(status==3){
            vo.setStatus(3);
            vo.setMsg("双方都同意约会～");
        }else  if(status==5){
            vo.setStatus(5);
            vo.setMsg("请等待对方到达～");
        }else  if(status==6){
            vo.setStatus(6);
            vo.setMsg("搞快点～");
        }else  if(status==7){
            vo.setStatus(6);
            vo.setMsg("双方到达指定位置");
        }
        else {
            vo.setStatus(0);
            vo.setMsg("快发起约会邀请～");
        }
        return vo;

    }


    public void makeFriendsRel(String userId, String targetId) {
        UserFriends userFriends = new UserFriends();
        userFriends.setId( CRCUtil.crc32Hex(targetId+userId));
        userFriends.setUserId(userId);
        userFriends.setFriendsId(targetId);
        userFriends.setFriendsStatus(1);
        userFriends.setCreatTime(new Timestamp(new Date().getTime()));
        userFriendsRepo.addFriends(userFriends);

        userFriends.setId( CRCUtil.crc32Hex(userId+targetId));
        userFriends.setUserId(targetId);
        userFriends.setFriendsId(userId);
        userFriendsRepo.addFriends(userFriends);
    }


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
        vo.setUserImage((String)e.get("user_face_image"));
        vo.setUserNickName((String)e.get("user_nickname"));
        return vo;}).collect(Collectors.toList());

    }


    public void removeFriendsRel(String userId, String targetId) {
        userFriendsRepo.updateFriendsStatus(userId,targetId);
    }

    public void delFriendsData(String userId, String targetId) {
        userFriendsRepo.delFriends( userId,targetId);
    }

    public FriendsVO getFriendsVO(String userId, String targetId) {
        FriendsVO vo = new FriendsVO();
        UserPersonalInfo up =  userRepository.queryUserPerInfo(targetId);
        vo.setUserId(targetId);
        vo.setUserNickName(up.getUserNickname());
        vo.setUserImage(up.getUserFaceImage());
        Integer friendsStatus =  userFriendsRepo.queryFriendsStatus(userId,targetId);
        vo.setFriendsStatus(friendsStatus);
        return vo;
    }


}
