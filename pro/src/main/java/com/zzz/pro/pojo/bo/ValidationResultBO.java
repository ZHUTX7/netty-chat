package com.zzz.pro.pojo.bo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author zhutianxiang
 * @Description TODO
 * @Date 2023/10/16 15:30
 * @Version 1.0
 */
@Data
@Component
public class ValidationResultBO {
    private long logId;
    private String conclusion;
    private int conclusionType;
    private List<ValidationDataBO> data;

    @Data
    class   ValidationDataBO{
        private int type;
        private int subType;
        private String conclusion;
        private int conclusionType;
        private double probability;
        private String msg;
        private List<String> codes;
        private String stars;
        private String datasetName;
        private double completeness;
        private String hits;
    }
}

