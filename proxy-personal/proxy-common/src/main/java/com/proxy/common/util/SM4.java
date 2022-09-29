package com.proxy.common.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * @author : ztx
 * @version :V1.0
 * @description : 国密SM4 加解密
 * @update : 2021/5/11 14:18
 */
public class SM4 {
    //key必须16位
    private static final String KEY = "1234567890123456";

    private static SymmetricCrypto sm4 = SmUtil.sm4(KEY.getBytes());


    public static SymmetricCrypto getSM4(){
        return sm4;
    }

}
