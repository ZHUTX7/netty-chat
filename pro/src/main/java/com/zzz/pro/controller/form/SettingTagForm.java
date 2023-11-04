package com.zzz.pro.controller.form;

import com.zzz.pro.pojo.bo.SettingBO;
import lombok.Data;

import java.util.List;

@Data
public class SettingTagForm {
    private String userId;
    private List<SettingBO> dataList;
}
