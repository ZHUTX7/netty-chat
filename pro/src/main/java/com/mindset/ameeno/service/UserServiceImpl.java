package com.mindset.ameeno.service;

import com.mindset.ameeno.controller.form.*;
import com.mindset.ameeno.controller.vo.LoginResultVO;
import com.mindset.ameeno.controller.vo.UserGpsVO;
import com.mindset.ameeno.controller.vo.UserPhotoVO;
import com.mindset.ameeno.controller.vo.UserVO;
import com.mindset.ameeno.enums.RedisKeyEnum;
import com.mindset.ameeno.enums.UserRoleEnum;
import com.mindset.ameeno.mapper.*;
import com.mindset.ameeno.pojo.dto.AmeenoCredit;
import com.mindset.ameeno.pojo.dto.UserBaseInfo;
import com.mindset.ameeno.pojo.dto.UserPersonalInfo;
import com.mindset.ameeno.pojo.dto.UserPhoto;
import com.mindset.ameeno.utils.*;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
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

    @Value("${ameeno.mode}")
    private String ameenoMode = "dev";
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
    @Resource
    private UserVOCache voCache;
    @Resource
    private RecommendPoolService recommendPoolService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private AmeenoCreditService creditService;

    private IDWorker idWorker = new IDWorker(1, 1, 1);

    @Override
    public LoginResultVO userLogin(LoginForm loginForm) {
        String code = null;
        // 1. 验证验证码
        code =  redisStringUtil.get(loginForm.getUserPhone());

        //--------上线前注释-------
        //--测试账号
        if(loginForm.getUserPhone().equals("00000000000")){
            code = "000000";
        }
        //---------------

        if (  StringUtils.isEmpty(code) ||
                (ameenoMode.equals("prod") &&!code.equals(loginForm.getVerifyCode()) )
        ){
            throw new ApiException(ResultEnum.LOGIN_VERIFY_CODE_ERROR.getCode(), "验证码错误");
        }
        LoginResultVO vo = new LoginResultVO();

        UserBaseInfo userBaseInfo = userBaseInfoMapper.selectByPhone(loginForm.getUserPhone());

        // 1. 验证用户是否存在
        if (userBaseInfo == null) {
            userBaseInfo =   newUserDataInit(loginForm.getUserPhone(), loginForm.getDeviceId());
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
            voCache.deleteUserVO(userBaseInfo.getUserId());
            return ResultVOUtil.success("删除成功");
        } else {
            return ResultVOUtil.error(ResultEnum.FAILED.getCode(), "删除失败，用户不存在");
        }

    }

    @Deprecated
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
            voCache.deleteUserVO(form.getUserId());
            return ResultVOUtil.success("更新用户资料成功", null);
        } else {
            return ResultVOUtil.error(ResultEnum.FAILED.getCode(), "更新用户资料失败");
        }
        //动态更新

    }

    @Override
    public UserVO queryUserVO(String userId) {
        return voCache.getUserVO(userId);
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

        Map<String,Integer> map = form.getMap();
        //循环map
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String photoId = entry.getKey();
            Integer targetIndex = entry.getValue();
            userPhotoMapper.updatePhotoIndex(photoId, targetIndex);
        }
        //删除用户userVO
        voCache.deleteUserVO(form.getUserId());
    }

    @Override
    public void userRealAuth(UserRealAuthForm form) {
        //修改用户real_auth信息
        userPersonalInfoMapper.updateUserAuth(form.getUserId(), 1);
        voCache.deleteUserVO(form.getUserId());
    }

    @Override
    public void updatePhone(UpdatePhoneForm form) {
        String code = redisStringUtil.get(form.getNewPhone());
        if(StringUtils.isEmpty(code) || !form.getCode().equals(code)){
            throw new ApiException(ResultEnum.LOGIN_VERIFY_CODE_ERROR.getCode(),"验证码错误");
        }
        if( userBaseInfoMapper.checkPhone(form.getNewPhone()) > 0){
            throw new ApiException(ResultEnum.PHONE_IS_EXIST.getCode(),ResultEnum.PHONE_IS_EXIST.getTitle());
        }
        //修改用户手机号
        userBaseInfoMapper.updateUserPhoneByUserId(form.getUserId(), form.getNewPhone());
    }

    @Override
    public void deleteUserBlackList(String userId) {
        recommendPoolService.removeBlackPool(userId);
    }


    //新用户创建数据初始化

    @Async
    @Override
    public UserBaseInfo newUserDataInit(String phone,String deviceId){
        //1. 用户基础信息初始化
        UserBaseInfo baseInfo =   userRegister(phone,deviceId);
        //2. 用户信誉信息初始化
        creditService.userCreditInit(baseInfo.getUserId(),phone);
        //3. 用户角色信息
        userRoleService.userRoleInit(baseInfo.getUserId());
        return baseInfo;
    }

    @Override
    public void userLogout(String userId) {
        //用户退出
        //清理deviceID
        //清理channel
        redisStringUtil.del(RedisKeyEnum.USER_DEVICE_ID.getCode()
                + userId);
        //清理定位池
        mapService.remove(userId);
    }
}

