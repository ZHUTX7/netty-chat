package com.mindset.ameeno.controller.form;

import lombok.Data;

import java.util.List;

@Data
public class DeleteSettingForm {
    private List<String> keys;
    private String userId;
}
