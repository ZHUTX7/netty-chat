package com.zzz.pro.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.zzz.pro.config.CosConfig;
import com.zzz.pro.dao.ChatMsgRepository;
import com.zzz.pro.dao.UserRepository;
import com.zzz.pro.dao.UserTagRepository;
import com.zzz.pro.enums.RedisKeyEnum;
import com.zzz.pro.enums.UserRoleEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.UserMatchMapper;
import com.zzz.pro.mapper.UserPhotoMapper;
import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.pojo.form.LoginForm;
import com.zzz.pro.pojo.form.UpdatePhotoIndexForm;
import com.zzz.pro.pojo.form.UpdateProfileForm;
import com.zzz.pro.pojo.form.UserGpsForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.pojo.vo.*;
import com.zzz.pro.utils.IDWorker;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.RedisUtil;
import com.zzz.pro.utils.ResultVOUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMatchMapper userMatchMapper;
    @Resource
    private UserRepository userRepository;
    @Resource
    private UserTagRepository userTagRepository;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ChatMsgRepository chatMsgRepository;
    @Resource
    private COSClient cosClient;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserPhotoMapper userPhotoMapper;

    private IDWorker idWorker = new IDWorker(1,1,1);

    @Override
    public LoginResultVO userLogin(LoginForm loginForm) {
        // 1. 验证验证码
//        redisUtil.set(loginForm.getUserPhone(),loginForm.getVerifyCode(),60*5);
        String code = (String)redisUtil.get(loginForm.getUserPhone());
        if( StringUtils.isEmpty(code) || !code.equals(loginForm.getVerifyCode())){
            throw new ApiException(401,"验证码错误");
        }
        LoginResultVO vo = new LoginResultVO();
        UserBaseInfo userBaseInfo = userRepository.getUserByPhone(loginForm.getUserPhone());
        // 1. 验证用户名是否存在
        if(userBaseInfo==null){
            //创建用户
            userBaseInfo=  userRegister(loginForm.getUserPhone(),loginForm.getDeviceId());
            vo.setIsNewUser(1);

        }else {
            vo.setIsNewUser(0);
            vo.setLastLoginTime(userBaseInfo.getLastLoginTime());
        }
        //2.封装VO
        vo.setUserId(userBaseInfo.getUserId());
        vo.setUserPhone(userBaseInfo.getUserPhone());
        vo.setUserRole(userBaseInfo.getUserRole());

        // 4. 创建token
        Map<String ,String > info =  new HashMap<>();
        info.put("userRole",userBaseInfo.getUserRole()+"");
        info.put("userId",userBaseInfo.getUserId());
        info.put("deviceId",userBaseInfo.getDeviceId());
        String token = JWTUtils.creatToken(info);
        vo.setToken(token);

        //
        redisUtil.set(RedisKeyEnum.USER_DEVICE_ID.getCode()
                +userBaseInfo.getUserId(),userBaseInfo.getDeviceId());
        // 5. 更新用户登录时间
        return vo;
    }

    @Override
    public UserBaseInfo userRegister(String phone,String deviceId) {
        UserBaseInfo userBaseInfo = new UserBaseInfo();
        userBaseInfo.setUserId(idWorker.nextId()+"");
        userBaseInfo.setUserPhone(phone);
        userBaseInfo.setDeviceId(deviceId);
        userBaseInfo.setLastLoginTime(new Date());
        userBaseInfo.setUserRole(UserRoleEnum.NORMAL_ROLE.getCode());
        userBaseInfo.setUserPassword("");
        userRepository.addUserBaseInfo(userBaseInfo);
        UserPersonalInfo userPersonalInfo = new UserPersonalInfo();
        userPersonalInfo.setUserId(userBaseInfo.getUserId());
        userRepository.addUserPersonalInfo(userPersonalInfo);
        return userBaseInfo;
    }

    @Override
    public SysJSONResult delUser(UserBaseInfo userBaseInfo) {
        int result =  userRepository.delUser(userBaseInfo);
        if(result == 1) {
            return ResultVOUtil.success("删除成功");
        }
        else{
            return ResultVOUtil.error(500,"删除失败，用户不存在");
        }

    }

    @Override
    public SysJSONResult uploadFaceImg(UserPersonalInfo userPersonalInfo) {
        int result =    userRepository.updateUserFace(userPersonalInfo);
        if(result == 1) {
            return ResultVOUtil.success("更新头像成功");
        }
        else{
            return ResultVOUtil.error(500,"更新头像失败");
        }
    }

    @Override
    public SysJSONResult userLoginByToken(String token) {
        if(StringUtil.isNullOrEmpty(token)){

            return  ResultVOUtil.error(500,"用户登录状态过期");
        }
        int token_code =  (int)JWTUtils.verify(token).get("token_code");
        if(token_code != 1 ){
            return ResultVOUtil.error(500,"用户登录状态过期");
        }
        String userId = JWTUtils.getClaim(token,"userId");
        UserPersonalInfo u = userRepository.queryUserPerInfo(userId);


        Map<String,Object> map = new HashMap<>();
        map.put("userId",u.getUserId());
        map.put("token",token);
        map.put("userPersonalInfo",u);
        return ResultVOUtil.success(map);

    }

    @Override
    public SysJSONResult updateUserProfile(UpdateProfileForm form) {
        UserPersonalInfo u = new UserPersonalInfo();
        BeanUtils.copyProperties(form,u);
        int result =  userRepository.updateUserProfile(u);
        return ResultVOUtil.success(null,"更新用户资料成功");
    }

    @Override
    public UserPersonalInfo queryUserProfile(String userId) {
        UserPersonalInfo u =  userRepository.queryUserPerInfo(userId);
        return u;
    }

    @Override
    public void changeUserGps(String userId,String gps) {
        redisTemplate.opsForValue().set(RedisKeyEnum.USER_POSITION.getCode()+userId,gps);
    }

    @Override
    public void updateUserTag(UserTag userTag) {
        userTagRepository.updateUserTag(userTag);
    }


    @Override
    public List<UserTagVO>  queryUserTag(String userId) {
        List<UserTag > list = userTagRepository.queryUserTag(userId);
        List<UserTagVO> result = list.stream().map(e->{
            UserTagVO vo = new UserTagVO();
            BeanUtils.copyProperties(e,vo);
            return vo;
        }).collect(Collectors.toList());

        return result;
    }

    @Override
    public void addUserTag(UserTag userTag) {
        try{
            userTagRepository.insertUserTag(userTag);
        }catch (Exception e){
            e.printStackTrace();
            throw new ApiException(401,"数据错误");
        }

    }

    @Override
    public void clearUserTag(UserTag userTag) {
        try{
            userTagRepository.delUserTag(userTag.getUserId(),userTag.getUserKey());
        }catch (Exception e){
            throw new ApiException(401,"数据错误");
        }

    }

    @Override
    public void uploadUserPos(UserGpsForm userGpsForm) {
        String gps = userGpsForm.getUserGps();
        String distance = userGpsForm.getDistance();
        if(!StringUtils.isEmpty(distance)){
            redisTemplate.opsForValue().set(RedisKeyEnum.USER_DISTANCE.getCode()+userGpsForm.getUserId(),distance);
        }
        redisTemplate.opsForValue().set(RedisKeyEnum.USER_POSITION.getCode()+userGpsForm.getUserId(),gps);
    }

    //目前只查询距离
    @Override
    public UserGpsVO queryUserPos(String  userId) {

        String distance=  (String)redisTemplate.opsForValue().get(RedisKeyEnum.USER_DISTANCE.getCode()+userId);

        UserGpsVO vo = new UserGpsVO();
        vo.setUserId(userId);
        vo.setDistance(distance);
        return vo;
    }

    @Override
    public String uploadUserPhoto(MultipartFile multipartFile, String userId,Integer photoIndex) {
        //文件在存储桶中的key
        String key = userId + photoIndex;
        String fileName = multipartFile.getOriginalFilename();
        List<String> FILE_WHILE_EXT_LIST = Arrays.asList("JPG","PNG","JPEG","GIF");
        Assert.notNull(fileName,"File name can not be empty");
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!FILE_WHILE_EXT_LIST.contains(fileExtName.toUpperCase())){
            throw new ApiException(500,"文件格式不正确");
        }
        fileName = key + "." + fileExtName;
        //准备将MultipartFile类型转为File类型
        File file = null;
        try {
            //生成临时文件
            file = File.createTempFile("temp", null);
            //将MultipartFile类型转为File类型
            multipartFile.transferTo(file);
            //创建存储对象的请求
            PutObjectRequest putObjectRequest = new PutObjectRequest("photo-1305532292", fileName, file);
            //执行上传并返回结果信息
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            //获取上传结果
            String url ="https://photo-1305532292.cos.ap-beijing.myqcloud.com/"+fileName;

            //将url存入数据库
            UserPhoto userPhoto = new UserPhoto();
            userPhoto.setUserId(userId);
            userPhoto.setPhotoUrl(url);
            userPhoto.setPhotoIndex(photoIndex);
            userPhoto.setPhotoCreateTime(new Date());
            userRepository.insertUserPhoto(userPhoto);
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭客户端
            cosClient.shutdown();
            //删除临时文件
            file.delete();
        }
        return null;
    }

    @Override
    public List<UserPhotoVO> queryUserPhoto(String userId) {
        //从数据库查询用户照片
        List<UserPhoto> list = userRepository.queryUserPhoto(userId);
        //将UserPhoto转为UserPhotoVO
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<UserPhotoVO> result = list.stream().map(e->{
            UserPhotoVO vo = new UserPhotoVO();
            BeanUtils.copyProperties(e,vo);
            return vo;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public void updateUserPhotoIndex(UpdatePhotoIndexForm form) {
        String id = form.getUserId();
        //格式：photoId , targetIndex
        List<String> strs = form.getPhotoIndex();
        strs.forEach(e->{
            String[] arr = e.split(",");
            String photoId = arr[0];
            String targetIndex = arr[1];
            userPhotoMapper.updateUserPhotoIndex(id,photoId,Integer.parseInt(targetIndex));
        });


    }
}
