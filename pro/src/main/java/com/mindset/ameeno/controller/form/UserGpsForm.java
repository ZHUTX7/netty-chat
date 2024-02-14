package com.mindset.ameeno.controller.form;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserGpsForm {
    @NotNull
    private String userId;
    @NotNull
    private String userGps;
    private String distance;
}
