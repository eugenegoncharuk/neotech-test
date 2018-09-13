package com.neotech.task.service;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import com.neotech.task.exception.PhoneNumberIncorrectException;
import com.neotech.task.utils.HtmlReadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneNumberValidationService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    Map<String, Integer> countryPhoneNumberLengthsMap = new HashMap();

    @PostConstruct
    public void init() {
        logger.info("Initializing from the web wiki all the codes/countries lengths");

        try {
            initData();
        } catch (Exception e) {
            // TODO: read from the DB or even file
        }

        logger.info("Finished init from the web wiki all the codes/countries lengths");
    }

    public void initData() throws IOException {
        final String content = HtmlReadUtils.readUrl("https://en.wikipedia.org/wiki/List_of_mobile_telephone_prefixes_by_country#International_prefixes_table");
        String pattern = "<tr>.*?<\\/td>.*?<td.*?\\+([0-9]+).*?<\\/td>[^<]*<td.*?<\\/td>[^<]*<td.*?([0-9]+)<\\/td>";

        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher m = p.matcher(content);

        while (m.find()) {
            countryPhoneNumberLengthsMap.put(m.group(1), Integer.valueOf(m.group(2)));
        }
    }

    /**
     * Map doesn't contain all the data about particular country length numbers in the world
     * 
     * @param phoneNumber
     * @param countryCode
     */
    public void validate(String phoneNumber, String countryCode) {
        final Integer length = countryPhoneNumberLengthsMap.get(countryCode);
        final int domesticNumberLength = phoneNumber.length() - countryCode.length();

        if(length != null && length != domesticNumberLength){
            throw new PhoneNumberIncorrectException();
        }
    }

}
