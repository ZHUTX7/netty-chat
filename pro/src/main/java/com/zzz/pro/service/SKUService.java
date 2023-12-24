package com.zzz.pro.service;


import com.alibaba.fastjson.JSONObject;
import com.zzz.pro.controller.form.BuyForm;
import com.zzz.pro.controller.form.ConsumeSKUForm;
import com.zzz.pro.controller.form.RefundIosForm;
import com.zzz.pro.controller.vo.UserSkuVO;
import com.zzz.pro.enums.*;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.*;
import com.zzz.pro.pojo.bo.SkuProductBO;
import com.zzz.pro.pojo.dto.*;
import com.zzz.pro.utils.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;


/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/26 19:26
 * @Version 1.0
 */
@Service
@Slf4j
public class SKUService {
    @Resource
    private OderService oderService;
    @Resource
    private AmeenoProductMapper productMapper;
    @Resource
    private AmeenoSkuMapper skuMapper;
    @Resource
    private AmeenoSkuProductMapper skuProductMapper;
    @Resource
    private UserPropsBagsMapper userPropsBagsMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    RedisStringUtil redisStringUtil;
    @Resource
    UserBaseInfoMapper userBaseInfoMapper;
    @Resource
    JwsUtils jwsUtils;

    public  List<AmeenoSku>  queryAllSku(){
       List<AmeenoSku> list =  skuMapper.selectBySkuSalesStatus(SkuStatsEnum.SALE.getCode());
       return list;
    }


    // "推送产品/服务"
    @Transactional(propagation = Propagation.REQUIRED)
    public void pushSKU(String orderId) {
       AmeenoOrders oder =  oderService.queryOrders(orderId);

       String skuId = oder.getBuySkuId();
       //1.查询SKU商品条目
       List<SkuProductBO> list =  skuProductMapper.selectBySkuId(skuId);
       if(CollectionUtils.isEmpty(list)){
           throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "SKU商品条目为空");
       }
       //2.查询SKU
       list.stream().forEach(e->{
           String userId = oder.getUserId();
           Integer productType = e.getProductType();
           //1 - "SUBSCRIBE" - 订阅
           if(productType == ProductTypeEnum.TIME.getCode()) {
               pushSubscribeSKU(e, userId);
           }
           //2 - "PURCHASE" - 次数类型
           else if(productType == ProductTypeEnum.COUNT.getCode()) {
               pushCountSKU(e, userId);
           }
           else if(productType == ProductTypeEnum.VIP.getCode()) {
               pushVipSKU(e, userId);
           }
           else {
               throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "产品类型错误");
           }
       });

    }


//    | 1- "SUBSCRIBE" - 订阅 2- "PURCHASE" - 单次购买 |

    public void pushSubscribeSKU(SkuProductBO bo,String userId) {
        int sumSubDays = SkuTimeUtils.getDay(bo.getTimeLimit(),bo.getTimeUnit());
        //1. 查询用户是否已经有订阅
        UserPropsBags userPropsBags =  userPropsBagsMapper.selectSubscribeSKU(userId,bo.getProductId());
        // 2. 如果没有订阅，新增订阅
        Date now =new Date();
        if(userPropsBags == null){
            userPropsBags = new UserPropsBags();
            userPropsBags.setUserId(userId);
            userPropsBags.setProductId(bo.getProductId());
            userPropsBags.setProductCount(1);
            //增加天数
            userPropsBags.setExpireTime(new Date(now.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userPropsBags.setGetTime(now);
            userPropsBags.setTimeLimitFlag(bo.getTimeLimit());
            userPropsBagsMapper.insert(userPropsBags);
        }
        // 3. 如果有订阅，更新订阅时间
        else {
            Date date =  userPropsBags.getExpireTime();
            //如果过期时间小于当前时间，从当前时间开始计算
            if(date.getTime() < now.getTime()) {
                date = now;
            }
            //增加天数
            userPropsBags.setExpireTime(new Date(date.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userPropsBags.setProductCount(1);
            userPropsBags.setTimeLimitFlag(bo.getTimeLimit());
            userPropsBagsMapper.updateByPrimaryKeySelective(userPropsBags);
        }
    }

    public void pushVipSKU(SkuProductBO bo,String userId) {
        int sumSubDays = SkuTimeUtils.getDay(bo.getTimeLimit(),bo.getTimeUnit());

        //1. 查询用户是否已经有订阅 svip -> ssvip 前端计算付费
        UserRole userRole  = userRoleMapper.selectByUserId(userId);

        // 2. 如果没有订阅，新增订阅
        Date now =new Date();
        if( userRole==null  ||  UserRoleEnum.NORMAL_ROLE.getCode().equals(userRole.getRoleType())  ){
            userRole = new UserRole();
            userRole.setId("role-"+userId);
            userRole.setUserId(userId);
            userRole.setRoleType(bo.getProductId());
            userRole.setExpireTime(new Date(now.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userRoleMapper.insert(userRole);
        }
        else if(UserRoleEnum.VIP_ROLE.getCode().equals(userRole.getRoleType())  ) {
            Date date =  userRole.getExpireTime();
            //如果过期时间小于当前时间，从当前时间开始计算
            if(date.getTime() < now.getTime()) {
                date = now;
            }
            //增加天数
            userRole.setRoleType(bo.getProductId());
            userRole.setExpireTime(new Date(date.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userRoleMapper.updateByPrimaryKeySelective(userRole);
        }
        // 3. 如果有订阅，更新订阅时间
        else {
            Date date =  userRole.getExpireTime();
            //如果过期时间小于当前时间，从当前时间开始计算
            if(date.getTime() < now.getTime()) {
                date = now;
            }
            //增加天数
            userRole.setRoleType(bo.getProductId());
            userRole.setExpireTime(new Date(date.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userRoleMapper.updateByPrimaryKeySelective(userRole);
        }
        userBaseInfoMapper.updateUserRole(userId,bo.getProductId());
        redisStringUtil.hdel(RedisKeyEnum.ALL_USER_VO.getCode() ,userId);
    }
    public void pushCountSKU(SkuProductBO bo,String userId) {
        Date now = new Date();
        UserPropsBags props = new UserPropsBags();
        props.setUserId(userId);
        props.setProductId(bo.getProductId());
        props.setProductCount(bo.getNums());
        props.setGetTime(now);
        //如果该SKU 没有有效期
        if(bo.getTimeLimit() <= 0){
            //nums +1
            props.setExpireTime(null);
            props.setTimeLimitFlag(0);
            userPropsBagsMapper.insertOrUpdateUserPropsBags(props);
        }
        //如果该SKU 有有效期
        Long expireTime =  bo.getTimeLimit() * 24 * 60 * 60 * 1000L;
        props.setExpireTime(new Date(now.getTime() + expireTime));
        userPropsBagsMapper.insert(props);
    }


    //消耗一次性产品SKU
    public void consumeProduct(ConsumeSKUForm form) {

        if(form.getNums()>1){
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "目前一次只能用一个");
        }
        UserPropsBags consumerSKU =   userPropsBagsMapper.selectOneSKU(form.getUserId(), form.getProductId());
        if(consumerSKU == null) {
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "用户产品数量不足");
        }
        if(consumerSKU.getTimeLimitFlag() == 1) {
            //消耗
            userPropsBagsMapper.deleteByPrimaryKey(consumerSKU.getId());
            return;
        }
        if(consumerSKU.getProductCount()<1){
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "用户产品数量不足");
        }
        consumerSKU.setProductCount(consumerSKU.getProductCount() - 1);
        userPropsBagsMapper.updateByPrimaryKeySelective(consumerSKU);
    }

    //查找我的道具
    public List<UserSkuVO> queryMyProps(String userId) {
        List<UserSkuVO> userPropsBags =  userPropsBagsMapper.selectMyAllSKU(userId);
        if(CollectionUtils.isEmpty(userPropsBags)){
            return Collections.emptyList();
        }
          return userPropsBags;
    }

    //apple notification
    public void notification( RefundIosForm form) {
        // TODO 记录每次的苹果通知请求
        String signedPayload= new String(Base64.getDecoder().decode(form.getSignedPayload().split("\\.")[0]));
        //解析苹果请求的数据
        JSONObject jsonObject=JSONObject.parseObject(signedPayload);
        ;
        Jws<Claims> result=jwsUtils.verifyJWT(jsonObject.getJSONArray("x5c").get(0).toString(),form.getSignedPayload());
        log.info("------receive IosSysMsg -------");
        log.info("msg is [{}]",result.toString());
        log.info("------ finished  -------");
//        String notificationType=result.getBody().get("notificationType").toString();
//        Claims map=result.getBody();
//        HashMap<String,Object> envmap=map.get("data",HashMap.class);
//        String env=envmap.get("environment").toString();
//
//        String resulttran= new String(Base64.getDecoder().decode(envmap.get("signedTransactionInfo").toString().split("\\.")[0]));
//        JSONObject jsonObjecttran=JSONObject.parseObject(resulttran);
//
//        Jws<Claims> result3=jwsUtils.verifyJWT(jsonObjecttran.getJSONArray("x5c").get(0).toString(),envmap.get("signedTransactionInfo").toString());
//        System.out.println(result3.getBody().toString());
////        HashMap<String,Object> orderMap=result3.getBody().("data",HashMap.class);
//        log.info("Apple store notification type is {} ,and env is {}：",notificationType,env);
//
//        if(notificationType.equals("DID_RENEW")) {
//            //比较原始订单
//
//            // VipOrder vipOrderinsert=new VipOrder();
//            String transactionId = result3.getBody().get("transactionId").toString();
//            String productId = result3.getBody().get("productId").toString();
//            String originalTransactionId = result3.getBody().get("originalTransactionId").toString();
//        }
//        else if(notificationType.equals("REFUND")) {
//
//            String originalTransactionId= result3.getBody().get("originalTransactionId").toString();
//            String productId = result3.getBody().get("productId").toString();
//            //逻辑代码
//        }
//        else {
//            log.info("notificationType未处理：" + notificationType);
//        }

    }

}
