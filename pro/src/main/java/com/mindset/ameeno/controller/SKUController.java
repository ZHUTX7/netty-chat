package com.mindset.ameeno.controller;

import com.alibaba.fastjson.JSONObject;
import com.mindset.ameeno.service.SKUService;
import com.mindset.ameeno.controller.form.BuyForm;
import com.mindset.ameeno.controller.form.ConsumeSKUForm;
import com.mindset.ameeno.controller.form.IosPaySuccessForm;
import com.mindset.ameeno.controller.form.RefundIosForm;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.service.OderService;
import com.mindset.ameeno.service.api.AppleService;
import com.mindset.ameeno.utils.JWTUtils;
import com.mindset.ameeno.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/10/27 18:31
 * @Version 1.0
 */
@RestController
@RequestMapping("/sku")
public class SKUController {
    @Resource
    private OderService oderService;
    @Resource
    private SKUService skuService;
    @Resource
    private AppleService appleService;

    @PostMapping("/query/all")
    public SysJSONResult queryAll(){
        return  ResultVOUtil.success(skuService.queryAllSku());
    }

    //查找我的道具
    @GetMapping("/query")
    public SysJSONResult<Object> queryMyProps(@RequestHeader("refreshToken") String token){
        String userId = JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(skuService.queryMyProps(userId));
    }

    //下单
    @PostMapping("/init/order")
    public SysJSONResult<Object> initOrder(@RequestHeader("refreshToken") String token, @RequestBody BuyForm buyForm){
        buyForm.setUserId(JWTUtils.getClaim(token,"userId"));
        return  ResultVOUtil.success( oderService.initOrder(buyForm));
    }

    //支付成功回调接口
    @PostMapping("/pay/callback")
    public SysJSONResult<Object>  payCallback(@RequestHeader("refreshToken") String token, @RequestBody IosPaySuccessForm form){
        oderService.updateOrder(form);
        return  ResultVOUtil.success("购买成功！",null);
    }

    //消耗道具
    @PostMapping("/init/consume")
    public SysJSONResult<Object>  skuConsume(@RequestHeader("refreshToken") String token, @RequestBody ConsumeSKUForm form){
        form.setUserId(JWTUtils.getClaim(token,"userId"));
        skuService.consumeProduct(form.getUserId(),form.getTargetId(),form.getProductId(),form.getNums());
        return  ResultVOUtil.success();
    }

    @GetMapping("/oder/status/query")
    public  SysJSONResult<Object>  orderStatusQuery(@RequestHeader("refreshToken") String token,
                                            @RequestHeader("orderId") String orderId){
        String userId = JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(oderService.queryOrderStatus(userId,orderId));
    }

    @PostMapping("/apple/listen")
    public Object appleListen(@RequestBody RefundIosForm form){
        appleService.notification(form);
        return ResultVOUtil.success();
    }

    @PostMapping("/restorePurchase")
    public Object appleListen(@Param("userId") String userId){
        skuService.restorePurchase(userId);
        return ResultVOUtil.success();
    }


}
