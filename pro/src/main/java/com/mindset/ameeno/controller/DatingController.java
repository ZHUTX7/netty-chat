package com.mindset.ameeno.controller;

import com.mindset.ameeno.service.DatingService;
import com.mindset.ameeno.enums.RelEnum;
import com.mindset.ameeno.enums.ResultEnum;
import com.mindset.ameeno.controller.form.DatingScoreForm;
import com.mindset.ameeno.pojo.result.SysJSONResult;
import com.mindset.ameeno.service.MapService;
import com.mindset.ameeno.service.RecommendPoolService;
import com.mindset.ameeno.service.api.MapSdkService;
import com.mindset.ameeno.utils.JWTUtils;
import com.mindset.ameeno.utils.ResultVOUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/8/3 00:00
 * @Version 1.0
 */

@RestController
@RequestMapping("/dating")
public class DatingController {
    @Resource
    private DatingService datingService;
    @Resource
    private MapService mapService;
    @Resource
    private MapSdkService mapSdkService;
    @Resource
    private RecommendPoolService recommendPoolService;

    @PostMapping("/complete")
    public SysJSONResult complete(@RequestHeader("refreshToken") String token, @RequestBody DatingScoreForm form){
        String userId =  JWTUtils.getClaim(token,"userId");
        datingService.completeDating(userId, form);
        return  ResultVOUtil.success();
    }

    //查看约会状态
    @GetMapping("/query")
    public SysJSONResult queryDatingStatus(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(datingService.queryDatingStatus(userId,targetId));
    }
    //提出约会/同意约会/修改约会状态
    @GetMapping("/accept")
    public SysJSONResult acceptDating(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId,
                                      @Param("action") Integer action){
        //action = 1 提出约会
        //action = 5 自己到达约会地点
        if(action != RelEnum.DATING_WAIT_ACCEPT.getCode() && action!=RelEnum.DATING_WAIT_ARRIVE.getCode()){
            return  ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),ResultEnum.PARAM_ERROR.getTitle());
        }
        String userId =  JWTUtils.getClaim(token,"userId");
        datingService.acceptDating(userId,targetId,action);
        return  ResultVOUtil.success();
    }

    //查找附近的人
    @GetMapping("/queryNearby")
    public SysJSONResult queryNearby(@RequestHeader("refreshToken") String token,@Param("count") Integer count,@Param("distance") Integer distance)  {
        String userId =  JWTUtils.getClaim(token,"userId");

        System.out.println("-------");
        System.out.printf(token);
        System.out.println("-------");
        if (StringUtils.isEmpty(userId)){
            return ResultVOUtil.error(4011,"token过期");
        }

        return  ResultVOUtil.success(recommendPoolService.pullUserList(0,userId,distance));
    }

    //计算两个人中心的点
    @GetMapping("/usersCenter/query")
    public SysJSONResult queryUsersCenter(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(mapService.calculateUserCenter(userId,targetId));
    }

    //推荐见面地点
    @GetMapping("/locale/recommend")
    public SysJSONResult recommendPoint(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(mapService.recommendLocale(userId,targetId,5));
    }


    //约会评分
    @PostMapping("/score")
    public SysJSONResult recommendPoint(@RequestHeader("refreshToken") String token, @RequestBody DatingScoreForm form){
        String userId =  JWTUtils.getClaim(token,"userId");
        datingService.datingEvaluate(userId,form);
        return  ResultVOUtil.success();
    }
    @PostMapping("/score2")
    public SysJSONResult recommendPoint2( @RequestBody DatingScoreForm form){
        datingService.datingEvaluate(form.getUserId(),form);
        return  ResultVOUtil.success();
    }
    //约会评分查看
    @GetMapping("/score/query")
    public SysJSONResult queryDatingScore(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        return  ResultVOUtil.success(datingService.queryDatingEvaluate(userId,targetId));
    }

    //约会过期
    @GetMapping("/delay")
    public SysJSONResult datingDelay(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId,
                                      @Param("action") Integer action){

        if(action != RelEnum.DATING_INCORRECT_DELAY.getCode()){
            return  ResultVOUtil.error(ResultEnum.PARAM_ERROR.getCode(),ResultEnum.PARAM_ERROR.getTitle());
        }
        String userId =  JWTUtils.getClaim(token,"userId");
        datingService.datingDelay(userId,targetId,action);
        return  ResultVOUtil.success();
    }

    @GetMapping("/skip")
    public SysJSONResult<Object> datingSkip(@RequestHeader("refreshToken") String token,@Param("targetId") String targetId){
        String userId =  JWTUtils.getClaim(token,"userId");
        datingService.datingSkipBySKU(userId,targetId);
        return  ResultVOUtil.success();
    }

}
