package com.neotech.task.app;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import com.neotech.task.exception.PhoneNumberIncorrectException;
import com.neotech.task.service.GraphNode;
import com.neotech.task.service.PhoneNumberCountryDetectService;
import com.neotech.task.service.PhoneNumberLengthCheckService;
import com.neotech.task.service.dto.CountryDto;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class PhoneNumberCountryDetectServiceTest {

    PhoneNumberCountryDetectService service;
            
    @Before
    public void init() {
        GraphNode[] phoneNumberGraph = new GraphNode[10];
        phoneNumberGraph[3] = new GraphNode();
        GraphNode node3 = phoneNumberGraph[3];

        node3.nextNodes = new GraphNode[10];
        node3.nextNodes[7] = new GraphNode();
        GraphNode node37 = node3.nextNodes[7];

        node37.nextNodes = new GraphNode[10];
        node37.nextNodes[1] = new GraphNode();
        GraphNode node371 = node37.nextNodes[1];
        node371.country = Optional.of("Latvia");
        node371.countryCode = "371";

        service = new PhoneNumberCountryDetectService(mock(PhoneNumberLengthCheckService.class), phoneNumberGraph);
    }

    @Test
    public void checkIdentifyOkTest() {
        final CountryDto c1 = service.identify("3712234234");
        assertEquals(c1.getCountry(), "Latvia");
        assertEquals(c1.getCountryCode(), "371");
    }

    @Test(expected = PhoneNumberIncorrectException.class)
    public void checkIdentifyNotOkTest() {
        service.identify("");
        service.identify(null);
        service.identify("37");
        service.identify("0");
    }
    
}
