package com.zzz.pro.controller;

import com.zzz.pro.controller.form.BuyForm;
import com.zzz.pro.controller.form.ConsumeSKUForm;
import com.zzz.pro.controller.form.IosPaySuccessForm;
import com.zzz.pro.pojo.result.SysJSONResult;
import com.zzz.pro.service.OderService;
import com.zzz.pro.service.SKUService;
import com.zzz.pro.service.api.AppleService;
import com.zzz.pro.utils.JWTUtils;
import com.zzz.pro.utils.ResultVOUtil;
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
        skuService.consumeProduct(form);
        return  ResultVOUtil.success();
    }
}
