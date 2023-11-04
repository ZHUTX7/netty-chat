package com.zzz.pro.controller.form;

import lombok.Data;

import java.util.List;

@Data
public class DeletePhotoForm {
    private List<String> photoIds;
}