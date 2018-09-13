package com.neotech.task.service;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import com.neotech.task.exception.PhoneNumberIncorrectException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class PhoneNumberLengthCheckServiceTest {

    @Test
    public void checkLengthOkTest() {
        final Map<String, Integer> data = new HashMap<>();
        data.put("371", 7);
        data.put("1345", 10);

        PhoneNumberLengthCheckService service = new PhoneNumberLengthCheckService(data);

        assertTrue(service.isValid("3711234567", "371"));
        assertTrue(service.isValid("13453711234567", "1345"));
    }

    @Test(expected = PhoneNumberIncorrectException.class)
    public void checkLengthNotValidTest() {
        final Map<String, Integer> data = new HashMap<>();
        data.put("371", 10);
        data.put("1345", 13);

        PhoneNumberLengthCheckService service = new PhoneNumberLengthCheckService(data);

        assertTrue(service.isValid("3711234567", "371"));
        assertTrue(service.isValid("13453711234567", "1345"));
        assertTrue(service.isValid("", "1345"));
        assertTrue(service.isValid("3711234567", ""));
        assertTrue(service.isValid("", ""));
        assertTrue(service.isValid(null, ""));
        assertTrue(service.isValid("", null));
        assertTrue(service.isValid(null, ""));
        assertTrue(service.isValid(null, null));
    }
    

}
