package com.zzz.pro.service;


import com.zzz.pro.controller.form.BuyForm;
import com.zzz.pro.controller.form.ConsumeSKUForm;
import com.zzz.pro.controller.vo.SkuVO;
import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.AmeenoProductMapper;
import com.zzz.pro.mapper.UserPropsBagsMapper;
import com.zzz.pro.mapper.UserRoleMapper;
import com.zzz.pro.pojo.dto.AmeenoOrders;
import com.zzz.pro.pojo.dto.AmeenoProduct;
import com.zzz.pro.pojo.dto.UserPropsBags;
import com.zzz.pro.pojo.dto.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;


/**
 * @Author zhutianxiang
 * @Description TODO
 * @Date 2023/10/26 19:26
 * @Version 1.0
 */
@Service
public class SKUService {
    @Resource
    private OderService oderService;
    @Resource
    private AmeenoProductMapper productMapper;
    @Resource
    private UserPropsBagsMapper userPropsBagsMapper;
    @Resource
    private UserRoleMapper userRoleMapper;



    // "推送产品/服务"
    public void pushSKU(String orderId) {
       AmeenoOrders oder =  oderService.queryOrders(orderId);

       String productId = oder.getBuyProductId();
       AmeenoProduct product =  productMapper.selectByPrimaryKey(productId);
       int nums = oder.getBuyNums() ;
       String userId = oder.getUserId();
       Integer productType = product.getProductType();
       //1 - "SUBSCRIBE" - 订阅
       if(productType == 1) {
           pushSubscribeSKU(product, nums, userId);
       }
       //2 - "PURCHASE" - 单次购买
       else if(productType == 2) {
              pushPurchaseSKU(product, nums, userId,product.getProductAvailableDay() * 24 * 60 * 60 * 1000L);
       }
       else if(productType == 3) {
           pushVipSKU(product, nums, userId);
       }
       else {
           throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "产品类型错误");
       }
    }


//    | 1- "SUBSCRIBE" - 订阅 2- "PURCHASE" - 单次购买 |

    public void pushSubscribeSKU(AmeenoProduct product,int nums,String userId) {
        int sumSubDays =  product.getProductAvailableDay() * nums;
        //1. 查询用户是否已经有订阅
        UserPropsBags userPropsBags =  userPropsBagsMapper.selectSubscribeSKU(userId,product.getProductId());
        // 2. 如果没有订阅，新增订阅
        Date now =new Date();
        if(userPropsBags == null){
            userPropsBags = new UserPropsBags();
            userPropsBags.setUserId(userId);
            userPropsBags.setProductId(product.getProductId());
            userPropsBags.setProductCount(nums);
            //增加天数
            userPropsBags.setExpireTime(new Date(now.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userPropsBags.setGetTime(now);
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
            userPropsBagsMapper.updateByPrimaryKeySelective(userPropsBags);
        }
    }

    public void pushVipSKU(AmeenoProduct product,int nums,String userId) {
        int sumSubDays =  product.getProductAvailableDay() * nums;
        //1. 查询用户是否已经有订阅 svip -> ssvip 前端计算付费
        UserRole userRole  = userRoleMapper.selectByUserId(userId);

        // 2. 如果没有订阅，新增订阅
        Date now =new Date();
        if(userRole == null){
            userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleType(product.getProductType());
            userRole.setExpireTime(new Date(now.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userRoleMapper.insert(userRole);
        }
        // 3. 如果有订阅，更新订阅时间
        else {
            Date date =  userRole.getExpireTime();
            //如果过期时间小于当前时间，从当前时间开始计算
            if(date.getTime() < now.getTime()) {
                date = now;
            }
            //增加天数
            userRole.setRoleType(product.getProductType());
            userRole.setExpireTime(new Date(date.getTime() + sumSubDays * 24 * 60 * 60 * 1000));
            userRoleMapper.updateByPrimaryKeySelective(userRole);
        }
    }
    public void pushPurchaseSKU(AmeenoProduct product,int nums,String userId,Long expireTime) {
        Date now = new Date();
        UserPropsBags sku = new UserPropsBags();
        sku.setUserId(userId);
        sku.setProductId(product.getProductId());
        sku.setProductCount(nums);
        sku.setGetTime(now);
        //如果该SKU 没有有效期
        if(expireTime == null){
            //nums +1
            sku.setExpireTime(null);
            sku.setTimeLimitFlag(0);
            userPropsBagsMapper.insertOrUpdateUserPropsBags(sku);
        }
        //如果该SKU 有有效期
        sku.setExpireTime(new Date(now.getTime() + expireTime));
        userPropsBagsMapper.insert(sku);
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
    public List<SkuVO> queryMyProps(String userId) {

        List<UserPropsBags> userPropsBags =  userPropsBagsMapper.selectMyAllSKU(userId);
        if(CollectionUtils.isEmpty(userPropsBags)){
            return Collections.emptyList();
        }
        List<SkuVO> voList = new ArrayList<>();
        userPropsBags.stream().forEach(e->{
            SkuVO vo = new SkuVO();
            vo.setProductId(e.getProductId());
            vo.setProductCount(e.getProductCount());
            vo.setExpireTime(e.getExpireTime().getTime());
            voList.add(vo);
        });
        return voList;
    }

}
