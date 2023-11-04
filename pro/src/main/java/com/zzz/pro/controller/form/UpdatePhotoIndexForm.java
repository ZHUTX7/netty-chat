package com.zzz.pro.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class UpdatePhotoIndexForm {
    @NotBlank(message = "用户id不能为空")
    String userId;
    Map<String,Integer> map;
}
