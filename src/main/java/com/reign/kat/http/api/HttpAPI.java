package com.reign.kat.http.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpAPI {
    private static final Logger log = LogManager.getLogger(HttpAPI.class);
    private final String host;
    private final String authToken;

    public HttpAPI(String host, String authToken) {
        this.host = host;
        this.authToken = authToken;
    }

    private HttpURLConnection createConnection(String host, String endpoint)
    {
        try {
            return (HttpURLConnection) new URL(new URL(host), endpoint).openConnection();
        } catch(MalformedURLException e)
        {
            log.warn("Malformed URL Connection attempted: ", e);
        } catch(IOException e)
        {
            log.warn("An IO Exception occurred in openConnection: ", e);
        }
        return null;
    }

    public String get(String endpoint)
    {
        HttpURLConnection con = createConnection(host, endpoint);
        if (con != null)
        {
            try {
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", authToken);

                int statusCode = con.getResponseCode();
                log.debug("HTTP STATUS CODE :: {}", statusCode);
                if (statusCode == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }

                    log.info(response.toString());
                    return response.toString();
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }

                    log.info(response.toString());
                    return response.toString();
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Send a POST request to the endpoint
     * @param endpoint URL endpoint to send the request to.
     * @param data JSON formatted string data.
     * @return String response from the server.
     */
    public String post(String endpoint, String data)
    {
        HttpURLConnection con = createConnection(host, endpoint);
        if (con != null)
        {
            try {
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", authToken);
                con.setDoOutput(true);

                try {
                    OutputStream os = con.getOutputStream();
                    byte[] input = data.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                } catch (IOException e)
                {
                    log.warn("Failed to encode POST request body ", e);
                }

                int statusCode = con.getResponseCode();
                log.debug("HTTP STATUS CODE :: {}", statusCode);
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null)
                {
                    response.append(line);
                }

                log.info(response.toString());
                return response.toString();

                } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
