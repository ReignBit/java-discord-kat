package com.reign.api.kat;

import com.reign.api.kat.models.ApiModel;
import com.reign.api.lib.providers.ApiHttpProvider;
import com.reign.api.lib.providers.ApiMongoProvider;
import com.reign.api.lib.providers.IApiProvider;
import com.reign.kat.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;


public class KatApi {
    private static final Logger log = LoggerFactory.getLogger(KatApi.class);

    /**
     * Initialize the selected data provider for the bot.
     * The selected provider will be responsible for fetching and saving guild and user data.
     * @param providerName Name of the provider, defaults to "http", options are:<br>
     *                     - http<br>
     *                     - mongodb<br>
     */
    public static void init(String providerName)
    {
        switch (providerName) {
            case "mongodb":
                log.debug("Using MongoDB...");
                KatApi.setProvider(new ApiMongoProvider(Config.MONGODB_URI, Config.MONGODB_NAME));
                break;
            case "http":
            default:
                log.debug("Using HTTP...");
                KatApi.setProvider(new ApiHttpProvider(Config.BACKEND_API_HOST, Config.BACKEND_API_KEY));
        }
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
}
