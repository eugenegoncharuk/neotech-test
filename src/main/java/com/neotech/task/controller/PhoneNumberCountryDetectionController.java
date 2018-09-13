package com.neotech.task.controller;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import com.neotech.task.service.dto.CountryDto;
import com.neotech.task.service.PhoneNumberCountryDetectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PhoneNumberCountryDetectionController {

    @Autowired
    private PhoneNumberCountryDetectService service;

    @ResponseBody
    @Validated
    @PostMapping("/country-detect")
    public CountryDto retrieveExchangeValue(@Valid @RequestBody PhoneRequest req){
        return service.identify(req.getPhone());
    }
}
