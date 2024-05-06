package com.reign.api.lib.providers;

import com.reign.api.kat.KatApi;
import com.reign.api.kat.models.ApiModel;
import com.reign.api.kat.responses.ApiResponse;
import com.reign.api.lib.JsonBodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class ApiHttpProvider implements IApiProvider
{
    protected static final Logger log = LoggerFactory.getLogger(ApiModel.class);

    private static final Duration HTTP_TIMEOUT_SECONDS = Duration.ofSeconds(5);
    private static final String API_HOST = KatApi.host;
    private static final String API_AUTH = KatApi.authStr;
    private static final HttpClient client = KatApi.getClient();


    @Override
    public <T extends ApiModel, Y extends ApiResponse<?>> boolean commit(String endpoint, T apiModel, Class<Y> responseClass)
    {
        Y resp =  post(endpoint, responseClass, apiModel);
        return resp != null;
    }

    @Override
    public <Y extends ApiResponse<?>> Y fetch(String endpoint, Class<Y> responseClass)
    {
        return get(endpoint, responseClass);
    }

    /**
     * Base method for all GET requests.
     * @param endpoint URI to request.
     * @param response JSON-Serializable class which the response will be transformed into.
     * @return T JSON-Deserialized class object
     * @param <T> Class to deserialize into.
     */
    private static <T> T get(String endpoint, Class<T> response) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(String.format("%s/%s", API_HOST, endpoint))
                )
                .setHeader("Authorization", API_AUTH)
                .timeout(HTTP_TIMEOUT_SECONDS)
                .build();

        log.trace("GET Requesting {}", String.format("%s/%s", API_HOST, endpoint));

        try
        {
            HttpResponse<Supplier<T>> resp = client.sendAsync(request, new JsonBodyHandler<>(response)).get();

            // Ok status codes - Client Error status codes
            if (resp.statusCode() >= 200 && resp.statusCode() < 400) {
                log.trace("Status code {}", resp.statusCode());
                return resp.body().get();
            }
            else{
                if (resp.statusCode() == 401)
                {
                    log.error("API Request returned status code 401.");
                    log.error("Ensure you have set `backend_api_key` in config.properties");
                }
                log.warn("Non-Ok status code ({}) received from POST {}", resp.statusCode(), String.format("%s/%s", API_HOST, endpoint));
                return resp.body().get();
            }


        } catch (ExecutionException | InterruptedException e)
        {
            log.error(String.valueOf(e));
            log.error("An error occurred whilst trying to GET request {}/{}", API_HOST, endpoint);
        }
        return null;
    }

    /**
     * Base method for all POST requests
     * @param endpoint Request URI.
     * @param response Class in which the response will be serialized into.
     * @param body JSON-Serializable object containing the body data.
     * @return Generic T; Serialized JSON data into an object of type T response.
     * @param <T> T JSON-Serializable class which the response will be serialized into.
     * @param <Y> Y JSON-Serializable object which the request body will be transformed from.
     */
    public <T,Y> T post(String endpoint, Class<T> response, Y body) {
        HttpRequest request = HttpRequest.newBuilder(
                        URI.create(String.format("%s/%s", API_HOST, endpoint))
                )
                .setHeader("Authorization", API_AUTH)
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        log.trace("POST Requesting {}", String.format("%s/%s", API_HOST, endpoint));
        log.trace("body: {}", body);
        try
        {
            HttpResponse<Supplier<T>> resp = client.sendAsync(request, new JsonBodyHandler<>(response)).get();

            // Ok status codes - Client Error status codes
            if (resp.statusCode() >= 200 && resp.statusCode() < 400) {
                log.trace("Status code {}", resp.statusCode());
            }
            else
            {
                log.warn("Non-Ok status code ({}) received from POST {}", resp.statusCode(), String.format("%s/%s", API_HOST, endpoint));
                log.error(body.toString());
            }

            return resp.body().get();
        } catch (ExecutionException | InterruptedException e)
        {
            log.error("An error occurred whilst trying to POST request {}/{}", API_HOST, endpoint);
        }
        return null;
    }
}
