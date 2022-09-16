package com.reign.api.kat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;


public class KatApi {
    private static final Logger log = LoggerFactory.getLogger(KatApi.class);
    private static final HttpClient client = HttpClient.newHttpClient();
    public static String host;
    public static String authStr;

    public static void setAuthorization(String host, String authentication) {
        KatApi.host = host;
        KatApi.authStr = authentication;

    }

    public static HttpClient getClient()
    {
        return client;
    }





}
