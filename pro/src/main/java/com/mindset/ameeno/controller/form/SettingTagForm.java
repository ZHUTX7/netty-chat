package com.mindset.ameeno.controller.form;

import com.mindset.ameeno.pojo.bo.SettingBO;
import lombok.Data;

import java.util.List;

@Data
public class SettingTagForm {
    private String userId;
    private List<SettingBO> dataList;
}
