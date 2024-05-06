package com.reign.api.kat.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reign.api.kat.ApiCache;
import com.reign.api.kat.KatApi;
import com.reign.api.kat.responses.ApiResponse;
import com.reign.api.lib.JsonBodyHandler;
import com.reign.api.lib.providers.IApiProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/* TODO:
    Want a way to not have to have a api running for the bot to function.
    In debug mode we just want to have a local way of storing/fetching models

    this class is where the fetching/saving happens.
    If debug mode is enabled we can change how we fetch data, i think...

    TODO:
    Having to do Endpoints.buildEndpoint(...) for each get/save is a bit ehhhhh.
    I think a nice way would be to have a static property like ApiModel.Endpoint
    and then having a method which we pass the required data.
 */

public abstract class ApiModel {
    protected static final Logger log = LoggerFactory.getLogger(ApiModel.class);

    public static IApiProvider provider;

    public String toString()
    {
        ObjectMapper map = new ObjectMapper();
        try {
            return map.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save the ApiModel to the Api.
     */
    public abstract boolean save();

    /**
     * Commit model data to the api provider.
     * @param endpoint Endpoint to send the data to.
     * @param apiModel Model to serialize.
     * @param responseClass Class of the response object
     * @return boolean if commit was successful.
     * @param <T> class of the model extends ApiModel
     * @param <Y> class of the response extends ApiResponse
     */
    protected <T extends ApiModel, Y extends ApiResponse<?>> boolean commit(String endpoint, T apiModel, Class<Y> responseClass)
    {
        return provider.commit(endpoint, apiModel, responseClass);
    }

    /**
     * Retrieves data from the API Provider.
     * @param endpoint Endpoint to fetch
     * @param responseClass Response class to serialize into.
     * @return complete response class
     * @param <Y> extends ApiResponse
     */

    protected static <Y extends ApiResponse<?>> Y fetch(String endpoint, Class<Y> responseClass)
    {

        return provider.fetch(endpoint, responseClass);
    }




}
