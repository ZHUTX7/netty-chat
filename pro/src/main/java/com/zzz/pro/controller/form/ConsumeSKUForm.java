package com.zzz.pro.controller.form;

import lombok.Data;

/**
 * @Author zhutianxiang
 * @Description TODO
 * @Date 2023/10/27 20:59
 * @Version 1.0
 */
@Data
public class ConsumeSKUForm {
     private int productId;
     private int  nums;
     private String userId;
     private String targetId;
}
