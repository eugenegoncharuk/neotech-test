package com.neotech.task.controller;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PhoneRequest {

    @NotNull
    // E.164 says 15 is max, phone vendors put 16 as longest
    // Minimal phone number is for Niue as of now: Format: +683 XXXX
    @Pattern(regexp = "[0-9]{7,16}")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
