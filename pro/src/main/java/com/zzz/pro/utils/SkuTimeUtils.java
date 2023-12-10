package com.zzz.pro.utils;

import com.zzz.pro.enums.ResultEnum;
import com.zzz.pro.exception.ApiException;

/**
 * @Author zhutianxiang
 * @Description 
 * @Date 2023/11/6 19:55
 * @Version 1.0
 */

public  class  SkuTimeUtils {
    static final String TIME_UNIT_DAY = "DAY";
    static final String TIME_UNIT_WEEK = "WEEK";
    static final String TIME_UNIT_MONTH = "MONTH";
    static final String TIME_UNIT_YEAR = "YEAR";

    public static int getDay(int timeLimit,String timeUnit) {
       if(timeUnit .equals(TIME_UNIT_DAY)) {
           return timeLimit;
         }else if(timeUnit .equals(TIME_UNIT_WEEK)) {
              return timeLimit * 7;
            }else if(timeUnit .equals(TIME_UNIT_MONTH)) {
                 return timeLimit * 30;
               }else if(timeUnit .equals(TIME_UNIT_YEAR)) {
                    return timeLimit * 365;
                  }else {
                    throw new ApiException(ResultEnum.PARAM_ERROR.getCode(), "时间单位错误");
       }
    }
}
