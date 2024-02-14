package com.mindset.ameeno.netty.enity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SystemMsg implements Serializable {

    private static final long serialVersionUID = -1L;

    private double[] gps ;
    private List<String> msgList;
    private String deviceId;

}
