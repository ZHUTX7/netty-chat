package com.zzz.pro.service;

import com.zzz.pro.controller.form.BuyForm;
import com.zzz.pro.controller.form.IosPaySuccessForm;
import com.zzz.pro.controller.vo.OrderStatusVO;
import com.zzz.pro.enums.OrderEnum;
import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.enums.SkuStatsEnum;
import com.zzz.pro.exception.ApiException;
import com.zzz.pro.mapper.AmeenoOrdersMapper;
import com.zzz.pro.mapper.AmeenoProductMapper;
import com.zzz.pro.mapper.AmeenoSkuMapper;
import com.zzz.pro.pojo.dto.AmeenoOrders;
import com.zzz.pro.pojo.dto.AmeenoProduct;
import com.zzz.pro.pojo.dto.AmeenoSku;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.service.api.AppleService;
import com.zzz.pro.utils.OrderIdGenerate;
import com.zzz.pro.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author zhutianxiang
 * @Description 订单service
 * @Date 2023/10/26 13:36
 * @Version 1.0
 */
@Slf4j
@Service
public class OderService {

    @Resource
    private AmeenoProductMapper productMapper;
    @Resource
    private AmeenoOrdersMapper ordersMapper;
    @Resource
    AppleService appleService;
    @Resource
    private SKUService skuService;
    @Resource
    private AmeenoSkuMapper skuMapper ;

    //订单初始化
    @Transactional
    public String initOrder(BuyForm form) {
        try{
            String userId = form.getUserId();
            String skuId = form.getSkuId();

            AmeenoSku sku =  skuMapper.selectByPrimaryKey(skuId);
            if(sku == null || !SkuStatsEnum.SALE.getCode().equals(sku.getSkuSalesStatus())){
                log.error(  "userId : [{}] ,购买商品 [{}]不存在或已下架",userId,skuId);
                throw new ApiException(ResultEnum.SKU_NOT_EXIST.getCode(),ResultEnum.SKU_NOT_EXIST.getTitle());
            }
            BigDecimal amount = sku.getSkuPrice();
            AmeenoOrders order = new AmeenoOrders();
            String oderId = OrderIdGenerate.getOrderId(userId);
            order.setOrderId(oderId);
            order.setUserId(userId);
            order.setOrderCreateTime(new Date());
            order.setOrderUpdateTime(new Date());
            order.setOrderFinishedTime(null);
            order.setTotalAmount(amount);
            order.setBuySkuId(skuId);
            order.setBuyNums(form.getBuyCount());
            //'PENDING','PAID','FAILED'
            order.setPaymentStatus(OrderEnum.PENDING.getCode());
            order.setPaymentMethod(null);
            order.setPaymentDate(null);
            order.setPaymentId(null);
            ordersMapper.insert(order);
            return oderId;
        }catch (Exception e){
            log.error("订单初始化异常",e);
            throw new ApiException(ResultEnum.ORDER_INIT_FAILED.getCode(),ResultEnum.ORDER_INIT_FAILED.getTitle());
        }
    }

    //支付成功  - 订单更新
    @Transactional
    public SysJSONResult updateOrder(IosPaySuccessForm form) {
        if(!appleService.verifyTransaction(form.getOrderId())){
            log.error("交易验证失败");
            return ResultVOUtil.error(ResultEnum.ORDER_UPDATE_FAILED.getCode(),ResultEnum.ORDER_UPDATE_FAILED.getTitle());
        }


        AmeenoOrders order =  ordersMapper.selectByPrimaryKey(form.getOrderId());
        Date now = new Date();
        order.setOrderUpdateTime(now);
        order.setOrderFinishedTime(now);
        order.setPaymentStatus(OrderEnum.PAID.getCode());
        order.setPaymentMethod("APPLE_PAY");
        order.setPaymentDate(now);
        order.setPaymentId(order.getPaymentId());
        ordersMapper.updateByPrimaryKeySelective(order);
        skuService.pushSKU(form.getOrderId());
        return ResultVOUtil.success();

        // return ResultVOUtil.error(ResultEnum.ORDER_UPDATE_FAILED.getCode(),ResultEnum.ORDER_UPDATE_FAILED.getTitle());

    }

    public AmeenoOrders queryOrders(String orderId) {
        return ordersMapper.selectByPrimaryKey(orderId);
    }

    public  List<OrderStatusVO> queryOrderStatus(String userId,String orderId){
        List<AmeenoOrders> list = new ArrayList<>();
        List<OrderStatusVO> voList = new ArrayList<>();
        if(!StringUtils.isEmpty(userId)){
            list =  ordersMapper.queryByUserId(userId);
            list.stream().forEach(e->{
                OrderStatusVO vo = new OrderStatusVO();
                BeanUtils.copyProperties(e,vo);
                voList.add(vo);
            });
        }
        else if(!StringUtils.isEmpty(orderId)){
            AmeenoOrders orders = ordersMapper.selectByPrimaryKey(orderId);
            OrderStatusVO vo = new OrderStatusVO();
            BeanUtils.copyProperties(orders,vo);
            voList.add(vo);
        }
        else{
            throw new ApiException(ResultEnum.ORDER_NOT_EXIST.getCode(),ResultEnum.ORDER_NOT_EXIST.getTitle());
        }


        return voList;
    }
}
