package com.zzz.pro.service;

import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.enums.UserRoleEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.*;
import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.controller.form.*;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.controller.vo.*;
import com.zzz.pro.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("userService")
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserPersonalInfoMapper userPersonalInfoMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private RedisStringUtil redisStringUtil;
    @Resource
    private UserPhotoMapper userPhotoMapper;
    @Resource
    private UserBaseInfoMapper userBaseInfoMapper;
    @Resource
    private MapService mapService;

    private IDWorker idWorker = new IDWorker(1, 1, 1);

    @Override
    public LoginResultVO userLogin(LoginForm loginForm) {
        // 1. 验证验证码
//        redisUtil.set(loginForm.getUserPhone(),loginForm.getVerifyCode(),60*5);
        String code =  redisStringUtil.get(loginForm.getUserPhone());
        if (StringUtils.isEmpty(code) || !code.equals(loginForm.getVerifyCode())) {
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "验证码错误");
        }
        LoginResultVO vo = new LoginResultVO();

        //TODO 代码优化
        UserBaseInfo userBaseInfo = userBaseInfoMapper.selectByPhone(loginForm.getUserPhone());

        // 1. 验证用户是否存在
        if (userBaseInfo == null) {
            userBaseInfo = userRegister(loginForm.getUserPhone(), loginForm.getDeviceId());
            vo.setIsNewUser(1);
        }
        else {
            //创建用户
            String userId = userBaseInfo.getUserId();
            UserPersonalInfo upInfo = userPersonalInfoMapper.selectByPrimaryKey(userId);
            if(upInfo == null || StringUtils.isEmpty(upInfo.getUserNickname())){
                vo.setIsNewUser(1);
            } else {
                vo.setIsNewUser(0);
            }
        }

        vo.setLastLoginTime(userBaseInfo.getLastLoginTime());
        //2.封装VO
        vo.setUserId(userBaseInfo.getUserId());
        vo.setUserPhone(userBaseInfo.getUserPhone());
        vo.setUserRole(userBaseInfo.getUserRole());

        // 4. 创建token
        Map<String, String> info = new HashMap<>();
        info.put("userRole", userBaseInfo.getUserRole());
        info.put("userId", userBaseInfo.getUserId());
        info.put("deviceId", userBaseInfo.getDeviceId());
        //30分钟过期
        String token = JWTUtils.creatToken(info,30,Calendar.MINUTE);
        //7天过期
        String refreshToken = JWTUtils.creatToken(info,60*24*7,Calendar.MINUTE);
        vo.setToken(token);
        vo.setRefreshToken(refreshToken);
        // 5. 更新用户登录时间
        if(!StringUtils.isEmpty(userBaseInfo.getDeviceId())){
            userBaseInfo.setDeviceId(loginForm.getDeviceId());
            redisStringUtil.set(RedisKeyEnum.USER_DEVICE_ID.getCode()
                    + userBaseInfo.getUserId(), loginForm.getDeviceId());
        }
        userBaseInfo.setLastLoginTime(new Date());
        userBaseInfo.setDeviceId(loginForm.getDeviceId());
        userBaseInfoMapper.updateByPrimaryKey(userBaseInfo);
        log.info("用户登录成功，用户信息：{}", userBaseInfo);
        return vo;
    }

    @Override
    public UserBaseInfo userRegister(String phone, String deviceId) {
        UserBaseInfo userBaseInfo = new UserBaseInfo();
        String id = CRCUtil.crc32Hex(idWorker.nextId() + "");
        userBaseInfo.setUserId(id);
        userBaseInfo.setUserPhone(phone);
        userBaseInfo.setDeviceId(deviceId);
        userBaseInfo.setLastLoginTime(new Date());
        userBaseInfo.setUserRole(UserRoleEnum.NORMAL_ROLE.getCode());
        userBaseInfo.setUserPassword("");
        userBaseInfoMapper.insert(userBaseInfo);
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(userBaseInfo.getUserId());
        userPersonalInfoMapper.insert(userPersonalInfo);
        return userBaseInfo;
    }

    @Override
    public SysJSONResult delUser(UserBaseInfo userBaseInfo) {
        int result = userBaseInfoMapper.delete(userBaseInfo);
        if (result == 1) {
            return ResultVOUtil.success("删除成功");
        } else {
            return ResultVOUtil.error(ResultEnum.FAILED.getCode(), "删除失败，用户不存在");
        }

    }

    @Override
    public SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo) {
        int result = userPersonalInfoMapper.updateByPrimaryKeySelective(userPersonalInfo);
        if (result == 1) {
            return ResultVOUtil.success("更新头像成功");
        } else {
            return ResultVOUtil.error(ResultEnum.FAILED.getCode(), "更新头像失败");
        }
    }


    @Override
    public SysJSONResult updateUserProfile(UpdateProfileForm form) {
//        UserPersonalInfo u = new UserPersonalInfo();
//        BeanUtils.copyProperties(form, u);
//        int result = userPersonalInfoMapper.updateByPrimaryKey(u);
        int result = userPersonalInfoMapper.updateSelectUserPersonal(form);
        if (result == 1) {
            return ResultVOUtil.success("更新用户资料成功", null);
        } else {
            return ResultVOUtil.error(ResultEnum.FAILED.getCode(), "更新用户资料失败");
        }
        //动态更新

    }

    @Override
    public UserPersonalInfo queryUserProfile(String userId) {
        UserPersonalInfo u = userPersonalInfoMapper.selectByPrimaryKey(userId);
        return u;
    }

    @Override
    public void changeUserGps(String userId, double[] gps) {
        mapService.save(gps, userId);
        redisTemplate.opsForValue().set(RedisKeyEnum.USER_POSITION.getCode() + userId, gps);
    }

    @Override
    public void uploadUserPos(UserGpsForm userGpsForm) {
        String gps = userGpsForm.getUserGps();
        String distance = userGpsForm.getDistance();
        if (!StringUtils.isEmpty(distance)) {
            redisTemplate.opsForValue().set(RedisKeyEnum.USER_DISTANCE.getCode() + userGpsForm.getUserId(), distance);
        }
        redisTemplate.opsForValue().set(RedisKeyEnum.USER_POSITION.getCode() + userGpsForm.getUserId(), gps);
    }

    //目前只查询距离
    @Override
    public UserGpsVO queryUserPos(String userId) {

        String distance = (String) redisTemplate.opsForValue().get(RedisKeyEnum.USER_DISTANCE.getCode() + userId);

        UserGpsVO vo = new UserGpsVO();
        vo.setUserId(userId);
        vo.setDistance(distance);
        return vo;
    }

    @Override
    public List<UserPhotoVO> queryUserPhoto(String userId) {
        //从数据库查询用户照片
        List<UserPhoto> list = userPhotoMapper.queryUserPhotoList(userId);
        //将UserPhoto转为UserPhotoVO
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<UserPhotoVO> result = list.stream().map(e -> {
            UserPhotoVO vo = new UserPhotoVO();
            BeanUtils.copyProperties(e, vo);
            return vo;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public void updateUserPhotoIndex(UpdatePhotoIndexForm form) {
        //       String userId = form.getUserId();
        //格式：photoId , targetIndex
//        List<String> strs = form.getPhotoIndex();
//        strs.forEach(e->{
//            String[] arr = e.split(",");
//            String photoId = arr[0];
//            String targetIndex = arr[1];
//            userPhotoMapper.updateUserPhotoIndex(id,photoId,Integer.parseInt(targetIndex));
//        });
        Map<String,Integer> map = form.getMap();
        //循环map
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String photoId = entry.getKey();
            Integer targetIndex = entry.getValue();
            userPhotoMapper.updatePhotoIndex(photoId, targetIndex);
        }

    }

    @Override
    public void userRealAuth(UserRealAuthForm form) {
        //修改用户real_auth信息
        userPersonalInfoMapper.updateUserAuth(form.getUserId(), 1);
    }

}

