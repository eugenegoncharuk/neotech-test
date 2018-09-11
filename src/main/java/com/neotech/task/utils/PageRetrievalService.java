package com.neotech.task.utils;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageRetrievalService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public static final String PATTERN = "<a[^>]*>([ 0-9\\+]*?)<\\/a>[^<]*<a[^>]*title=\"([a-zA-Z ]*?)\"[^<]*<\\/a>";
    public static final String URL = "https://en.wikipedia.org/wiki/List_of_country_calling_codes";

    public static void main(String[] args) throws IOException {
        GraphNode[] phoneNumberGraph = processWithRegex();


        System.out.println(getFromGraph(phoneNumberGraph, "371233423423"));
        System.out.println(getFromGraph(phoneNumberGraph, "37244423423"));
        System.out.println(getFromGraph(phoneNumberGraph, "1456233423423"));
        System.out.println(getFromGraph(phoneNumberGraph, "223233423423"));
        System.out.println(getFromGraph(phoneNumberGraph, "233233423423"));
        System.out.println(getFromGraph(phoneNumberGraph, "563233423423"));
    }

    private static GraphNode[] processWithRegex() throws IOException {

        final String content = readHtml();

        Pattern p = Pattern.compile(PATTERN);
        Matcher m = p.matcher(content);

        GraphNode[] phoneNumberGraph = new GraphNode[10];
        while (m.find()) {
            final String numberStr = m.group(1).replaceAll("[^0-9]", "");
            final String country = m.group(2);

            insertIntoGraph(phoneNumberGraph, numberStr, country);
        }

        return phoneNumberGraph;
    }

    private static String readHtml() throws IOException {
        final URL oracle = new URL(URL);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));
        return IOUtils.toString(in);
    }

    private static void insertIntoGraph(GraphNode[] phoneNumberGraph, String number, String country) {
        final String digitS = number.substring(0, 1);
        final Integer digit = Integer.valueOf(digitS);
        if (phoneNumberGraph[digit] == null) {
            phoneNumberGraph[digit] = new GraphNode();
        }

        if (number.length() > 1) {
            insertIntoGraph(phoneNumberGraph[digit].nextNodes, number.substring(1), country);
        } else {
            phoneNumberGraph[digit].value = Optional.of(country);
        }
    }

    private static String getFromGraph(GraphNode[] phoneNumberGraph, String number) {
        final String digitS = number.substring(0, 1);
        final Integer digit = Integer.valueOf(digitS);

        final Optional<String> value = phoneNumberGraph[digit].value;
        if (value.isPresent()) {
            return value.get();
        }

        if (phoneNumberGraph[digit] == null || number.length() <= 1) {
            return null;
        }

        return getFromGraph(phoneNumberGraph[digit].nextNodes, number.substring(1));
    }

}
