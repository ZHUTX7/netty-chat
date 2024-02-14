package com.mindset.ameeno.controller.vo;

import lombok.Data;

@Data
public class UserPhotoVO {
    /**
     * 图片序号
     */
    private Integer photoIndex;

    /**
     * 图片ID
     */
    private String photoId  ;

    /**
     * 图片名称
     */
    private String photoName  ;
}
