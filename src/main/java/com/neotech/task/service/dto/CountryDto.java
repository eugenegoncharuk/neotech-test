package com.neotech.task.service.dto;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */
 
public class CountryDto {

    private String country;
    private String countryCode;

    public CountryDto(){}

    public CountryDto(String c, String code) {
        this.country = c;
        this.countryCode = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
