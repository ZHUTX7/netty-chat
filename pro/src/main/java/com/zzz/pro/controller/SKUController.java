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
 * @Description TODO
 * @Date 2023/10/27 18:31
 * @Version 1.0
 */
@RestController("/sku")
public class SKUController {
    @Resource
    private OderService oderService;
    @Resource
    private SKUService skuService;
    @Resource
    private AppleService appleService;

    //查找我的道具
    @PostMapping("/query")
    public SysJSONResult queryMyProps(@RequestHeader("refreshToken") String token){
        String userId = JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(skuService.queryMyProps(userId));
    }

    //下单
    @PostMapping("/init/order")
    public SysJSONResult initOrder(@RequestHeader("refreshToken") String token, @RequestBody BuyForm buyForm){
        buyForm.setUserId(JWTUtils.getClaim(token,"userId"));
        String id = oderService.initOrder(buyForm);
        return  ResultVOUtil.success("购买成功！",null);
    }

    //支付成功回调接口
    @GetMapping("/pay/callback")
    public SysJSONResult payCallback(@RequestHeader("refreshToken") String token, @RequestBody IosPaySuccessForm form){
        oderService.updateOrder(form);
        return  ResultVOUtil.success("购买成功！",null);
    }

    //消耗道具
    @PostMapping("/init/consume")
    public SysJSONResult skuConsume(@RequestHeader("refreshToken") String token, @RequestBody ConsumeSKUForm form){
        form.setUserId(JWTUtils.getClaim(token,"userId"));
        skuService.consumeProduct(form);
        return  ResultVOUtil.success();
    }
}
