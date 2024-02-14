package com.mindset.ameeno.controller.form;

import lombok.Data;

import java.util.List;

@Data
public class DeleteTagForm {
    private List<String> keys;
    private String userId;
}
