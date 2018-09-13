package com.neotech.task.service;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import com.neotech.task.exception.PhoneNumberIncorrectException;
import com.neotech.task.service.dto.CountryDto;
import com.neotech.task.utils.HtmlReadUtils;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneNumberCountryDetectService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String WORLD_EXCEPT_NORTH_AMERICA_PATTERN = "<a[^>]*>([ 0-9\\+]*?)<\\/a>[^<]*<a[^>]*title=\"([a-zA-Z ]*?)\"[^<]*<\\/a>";
    public static final String NORTH_AMERICA_PATTERN = "<tr>[^<]*<td><a[^>]*title=\"(.+?)\"[^<]*<\\/a>(.*?)<\\/tr>";
    public static final String NORTH_AMERICA_NUMBERS_PATTERN = ">([0-9]{3,3})<";

    public static final String AMERICA_PATTERN = "<span class=\"mw-headline\" id=\"United_States\">(.*?)<\\/table>";
    public static final String CANADA_PATTERN = "<span class=\"mw-headline\" id=\"Canada\">(.*?)<\\/table>";
    public static final String CARRIBEAN_PATTERN = "<span class=\"mw-headline\" id=\"Caribbean_and_Atlantic_Islands\">(.*?)<\\/table>";

    public static final String NORTH_AMERICA_WIKI_URL = "https://en.wikipedia.org/wiki/List_of_North_American_Numbering_Plan_area_codes";
    public static final String WORLD_WIKI_URL = "https://en.wikipedia.org/wiki/List_of_country_calling_codes";

    private static GraphNode[] phoneNumberGraph = new GraphNode[10];

    @Autowired
    PhoneNumberLengthCheckService validationService;

    @PostConstruct
    public void init() {
        logger.info("Initializing from the web wiki all the codes/countries");

        try {
            initData();
        } catch (Exception e) {
            // TODO: read from the DB or even file
        }

        logger.info("Finished init from the web wiki all the codes/countries");
    }

    private void initData() throws IOException {
        processWorldExceptNorthAmerica();
        processNorthAmerica();
    }

    @Nullable
    public CountryDto identify(String phoneNumber) {
        final Pair<String, String> pairRes = getFromGraph(phoneNumberGraph, phoneNumber);
        if (pairRes == null) {
            throw new PhoneNumberIncorrectException();
        }

        validationService.validate(phoneNumber, pairRes.getValue());
        
        return new CountryDto(pairRes.getKey(), pairRes.getValue());
    }

    private Pair<String, String> getFromGraph(GraphNode[] passedGraph, String number) {
        final String digitS = number.substring(0, 1);
        final Integer digit = Integer.valueOf(digitS);

        if (passedGraph[digit] == null) {
            return null;
        }

        final Optional<String> value = passedGraph[digit].country;
        if (value.isPresent()) {
            return new Pair(value.get(), passedGraph[digit].countryCode);
        }

        if (passedGraph[digit] == null || number.length() <= 1) {
            return null;
        }

        return getFromGraph(passedGraph[digit].nextNodes, number.substring(1));
    }

    private void processNorthAmerica() throws IOException {

        final String content = HtmlReadUtils.readUrl(NORTH_AMERICA_WIKI_URL);

        Pattern unitedStatesPattern = Pattern.compile(AMERICA_PATTERN, Pattern.DOTALL);
        final Matcher unitedStatesMatcher = unitedStatesPattern.matcher(content);
        if (unitedStatesMatcher.find()) {
            processNorthAmericaCountryContent(unitedStatesMatcher.group(1), "United States");
        }

        Pattern canadaPattern = Pattern.compile(CANADA_PATTERN, Pattern.DOTALL);
        final Matcher canadaMatcher = canadaPattern.matcher(content);
        if (canadaMatcher.find()) {
            processNorthAmericaCountryContent(canadaMatcher.group(1), "Canada");
        }

        Pattern carribeanPattern = Pattern.compile(CARRIBEAN_PATTERN, Pattern.DOTALL);
        final Matcher carribeanMatcher = carribeanPattern.matcher(content);
        if (carribeanMatcher.find()) {
            processNorthAmericaCountryContent(carribeanMatcher.group(1), "Carribean");
        }
    }

    private void processNorthAmericaCountryContent(String content, String country) {
        Pattern p = Pattern.compile(NORTH_AMERICA_PATTERN, Pattern.DOTALL);
        Matcher m = p.matcher(content);

        Pattern pNumbers = Pattern.compile(NORTH_AMERICA_NUMBERS_PATTERN, Pattern.DOTALL);

        while (m.find()) {
            final String countryState = m.group(1);
            final String numbersContent = m.group(2);

            Matcher matcherNumbers = pNumbers.matcher(numbersContent);
            while (matcherNumbers.find()) {
                final String numberStr = "1" + matcherNumbers.group(1);

                logger.debug("processNorthAmericaCountryContent: " + numberStr + "=" + country + ":" + countryState);

                insertIntoGraph(phoneNumberGraph, numberStr, country, "");
            }
        }
    }

    private void processWorldExceptNorthAmerica() throws IOException {

        final String content = HtmlReadUtils.readUrl(WORLD_WIKI_URL);
        Pattern p = Pattern.compile(WORLD_EXCEPT_NORTH_AMERICA_PATTERN, Pattern.DOTALL);
        Matcher m = p.matcher(content);

        while (m.find()) {
            final String numberStr = m.group(1).replaceAll("[^0-9]", "");
            if (numberStr.length() <= 1) {
                continue;
            }
            final String country = m.group(2);
            logger.debug("processWorldExceptNorthAmerica: " + numberStr + "=" + country);

            insertIntoGraph(phoneNumberGraph, numberStr, country, "");
        }
    }

    private void insertIntoGraph(GraphNode[] phoneNumberGraph, String number, String country, String countryCode) {
        final String digitS = number.substring(0, 1);
        countryCode += digitS;
        final Integer digit = Integer.valueOf(digitS);
        
        if (phoneNumberGraph[digit] == null) {
            phoneNumberGraph[digit] = new GraphNode();
        }

        if (number.length() > 1) {
            insertIntoGraph(phoneNumberGraph[digit].nextNodes, number.substring(1), country, countryCode);
        } else {
            phoneNumberGraph[digit].country = Optional.of(country);
            phoneNumberGraph[digit].countryCode = countryCode;
        }
    }
}
