package com.mindset.ameeno.pojo.bo;

import lombok.Data;

//短信
@Data
public class SmsBO {
    private String[] phoneNumberSet;
    private String sessionContext;
    private String signName;
    private String smsSdkAppId;
    private String templateId;
    private String[] templateParamSet;

}

