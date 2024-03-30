package com.mindset.ameeno.service;

import com.mindset.ameeno.enums.PayMethodEnum;
import com.mindset.ameeno.utils.OrderIdGenerate;
import com.mindset.ameeno.controller.form.BuyForm;
import com.mindset.ameeno.controller.form.IosPaySuccessForm;
import com.mindset.ameeno.controller.vo.OrderStatusVO;
import com.mindset.ameeno.enums.OrderEnum;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.enums.SkuStatsEnum;
import com.mindset.ameeno.exception.ApiException;
import com.mindset.ameeno.mapper.AmeenoOrdersMapper;
import com.mindset.ameeno.mapper.AmeenoProductMapper;
import com.mindset.ameeno.mapper.AmeenoSkuMapper;
import com.mindset.ameeno.pojo.dto.AmeenoOrders;
import com.mindset.ameeno.pojo.dto.AmeenoSku;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.service.api.AppleService;
import com.mindset.ameeno.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    @Resource
    SkuPushRecordService skuPushRecordService;

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
            String oderId = UUID.randomUUID().toString();
//            String oderId = OrderIdGenerate.getOrderId(userId);
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
        AmeenoOrders order =  ordersMapper.selectByPrimaryKey(form.getOrderId());
        Date now = new Date();
        order.setOrderUpdateTime(now);
        order.setOrderFinishedTime(now);
        order.setPaymentStatus(OrderEnum.PAID.getCode());
        order.setPaymentMethod(PayMethodEnum.IOS_MANUAL_PAY.getCode());
        order.setPaymentDate(now);
        order.setPaymentId(order.getPaymentId());
        ordersMapper.updateByPrimaryKeySelective(order);
        skuPushRecordService.addPushRecord(order.getOrderId(),order.getUserId(),form.getReceipt());
        skuService.pushSKU(form.getOrderId());
        return ResultVOUtil.success();

        // return ResultVOUtil.error(ResultEnum.ORDER_UPDATE_FAILED.getCode(),ResultEnum.ORDER_UPDATE_FAILED.getTitle());

    }

    public AmeenoOrders queryOrders(String orderId) {
        return ordersMapper.selectByPrimaryKey(orderId);
    }

    public AmeenoOrders queryOrdersByTransactionId(String transactionId) {
        return ordersMapper.queryOneByPaymentId(transactionId);
    }

    public List<AmeenoOrders> queryVipOrdersByUserId(String userId) {
        return ordersMapper.queryVipOrdersByUserId(userId);
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
