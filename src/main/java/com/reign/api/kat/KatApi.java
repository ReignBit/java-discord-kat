package com.reign.api.kat;

import com.reign.api.kat.models.ApiModel;
import com.reign.api.lib.providers.IApiProvider;
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

    /**
     * Sets the API Provider to be used when saving/loading data <br><br>
     *
     * Currently only have 2 providers:<br>
     *  - ApiHttpProvider: <i>Connects to a HTTP api at a remote source</i><br>
     *  - ApiLocalProvider: <i>Uses a file for storage. (Should only be used in dev environments!)</i><br>
     *
     * */
    public static void setProvider(IApiProvider provider)
    {
        ApiModel.provider = provider;
    }

    public static HttpClient getClient()
    {
        return client;
    }





}
