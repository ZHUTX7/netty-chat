package com.mindset.ameeno.service;


import com.alibaba.fastjson.JSONObject;
import com.mindset.ameeno.controller.form.ConsumeSKUForm;
import com.mindset.ameeno.controller.form.RefundIosForm;
import com.mindset.ameeno.controller.vo.UserSkuVO;
import com.mindset.ameeno.enums.*;
import com.mindset.ameeno.mapper.*;
import com.mindset.ameeno.pojo.bo.AppleReceiptBO;
import com.mindset.ameeno.pojo.dto.*;
import com.mindset.ameeno.utils.CRCUtil;
import com.mindset.ameeno.utils.JwsUtils;
import com.mindset.ameeno.utils.RedisStringUtil;
import com.mindset.ameeno.utils.SkuTimeUtils;
import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.pojo.bo.SkuProductBO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


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
    @Resource
    AmeenoOrdersMapper ordersMapper;
    @Resource
    UserSkuUsedRecordMapper skuUsedRecordMapper;
    @Resource
    SkuPushRecordService skuPushRecordService;
    public  List<AmeenoSku>  queryAllSku(){
       List<AmeenoSku> list =  skuMapper.selectBySkuSalesStatus(SkuStatsEnum.SALE.getCode());
       return list;
    }


    // "推送产品/服务"
    @Transactional(propagation = Propagation.REQUIRED)
    public void pushSKU(String orderId) {
       AmeenoOrders oder =  oderService.queryOrders(orderId);

       //检查发货状态
       if(skuPushRecordService.checkIsPushed(orderId)){
           return;
       };

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
        skuPushRecordService.skuPushFinished(orderId);
    }


//    | 1- "SUBSCRIBE" - 订阅 2- "PURCHASE" - 单次购买 |

    private void pushSubscribeSKU(SkuProductBO bo,String userId) {
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

    private void pushVipSKU(SkuProductBO bo,String userId) {
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
    private void pushCountSKU(SkuProductBO bo,String userId) {
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
    public void consumeProduct(String userId,String targetId,String productId,int nums) {

        if(nums>1){
            throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "目前一次只能用一个");
        }
        UserPropsBags consumerSKU =   userPropsBagsMapper.selectOneSKU(userId,productId);
        if(consumerSKU == null) {
            throw new ApiException(ResultEnum.PROPS_NOT_ENOUGH.getCode(), "用户产品数量不足");
        }
        if(consumerSKU.getTimeLimitFlag() == 1) {
            //消耗
            userPropsBagsMapper.deleteByPrimaryKey(consumerSKU.getId());
            return;
        }
        if(consumerSKU.getProductCount()<1){
            throw new ApiException(ResultEnum.PROPS_NOT_ENOUGH.getCode(), "用户产品数量不足");
        }
        consumerSKU.setProductCount(consumerSKU.getProductCount() - 1);
        userPropsBagsMapper.updateByPrimaryKeySelective(consumerSKU);
        UserSkuUsedRecord record = new UserSkuUsedRecord();
        record.setProductId(productId);
        record.setUserId(userId);
        record.setTargetId(targetId);
        record.setUseTime(new Date());
        record.setId(CRCUtil.crc32Hex(UUID.randomUUID().toString()));
        skuUsedRecordMapper.insert(record);
    }

    //查找我的道具
    public List<UserSkuVO> queryMyProps(String userId) {
        List<UserSkuVO> userPropsBags =  userPropsBagsMapper.selectMyAllSKU(userId);
        if(CollectionUtils.isEmpty(userPropsBags)){
            return Collections.emptyList();
        }
          return userPropsBags;
    }

    //续订
    @Transactional
    public void renew(AppleReceiptBO bo ){
        //1. 查找原订单, 通过订单查询用户ID 、 PRODUCT
        String trxId = bo.getOriginalTransactionId().toString();
        AmeenoOrders orders =   oderService.queryOrdersByTransactionId(trxId);
        //2. 创建续费订单
        AmeenoOrders newOrder = new AmeenoOrders();
        newOrder.setOrderId(bo.getTransactionId().toString()); // 订单ID
        newOrder.setUserId(orders.getUserId()); // 假定的用户ID
        newOrder.setOrderCreateTime(bo.getPurchaseDate()); // 设置当前时间为订单创建时间
        newOrder.setOrderUpdateTime(bo.getPurchaseDate()); // 设置当前时间为订单更新时间
        newOrder.setOrderFinishedTime(bo.getPurchaseDate()); // 订单完成时间（如果订单未完成，则可能为null）
        newOrder.setTotalAmount(bo.getPrice()); // 假定的订单总金额
        newOrder.setBuySkuId(bo.getProductId()); // 假定的购买商品SKU ID
        newOrder.setBuyNums(bo.getQuantity()); // 购买数量
        newOrder.setPaymentStatus(OrderEnum.PAID.getCode()); // 假定的订单支付状态，例如“UNPAID”
        newOrder.setPaymentMethod(PayMethodEnum.IOS_AUTO_RENEW_PAY.getCode()); // 假定的支付方式
        newOrder.setPaymentDate(bo.getPurchaseDate()); // 支付时间（如果订单未支付，则可能为null）
        newOrder.setPaymentId(bo.getTransactionId().toString()); // 支付订单ID（如果订单未支付，则可能为null）
        ordersMapper.insert(newOrder);
        //2. 续期 ，续期发放的道具怎么算
        pushSKU(newOrder.getOrderId());
        userPropsBagsMapper.updateExpireTime(orders.getUserId(),bo.getProductId(),bo.getExpiresDate());
        log.info("用户{} 续订{} 成功,到期时间：{}",orders.getUserId(),bo.getProductId(),bo.getExpiresDate());
    }

    //退订
    public void refund(AppleReceiptBO bo){
        String orderId = bo.getOriginalTransactionId().toString();
        AmeenoOrders orders =   oderService.queryOrders(orderId);
        log.info("用户{} 退订{} ",orders.getUserId(),bo.getProductId());
    }
    //

    public void restorePurchase(String userId){
        //恢复购买
        //1. 查询账单
        List<AmeenoOrders> orders =   oderService.queryVipOrdersByUserId(userId);

        //2. 账单复查
        List<String> orderIds = new ArrayList<>();
        orders.forEach(e->{
            if(e.getPaymentStatus().equals(OrderEnum.PAID)){
                orderIds.add(e.getOrderId());
            }
        });
        if(CollectionUtils.isEmpty(orderIds)){
            return;
        }
        //3.查询发货记录
        List<String> rePushOrderId =  skuPushRecordService.queryUnPushOrder(orderIds);

        for (String orderId : rePushOrderId){
            pushSKU(orderId);
        }
        return;

    }

}
