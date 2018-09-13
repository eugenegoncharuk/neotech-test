package com.neotech.task.utils;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class HtmlReadUtils {

    public static String readUrl(String url) throws IOException {
        final URL urk = new URL(url);
        final BufferedReader in = new BufferedReader(
                new InputStreamReader(urk.openStream()));
        return IOUtils.toString(in);
    }

}
