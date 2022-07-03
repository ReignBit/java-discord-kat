package com.reign.api.tenor;

import com.reign.api.TenorApi;
import com.reign.api.responses.tenor.TenorGifs;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class TenorApiTest {

    public static void main(String[] args) {
        TenorApi api = new TenorApi("", "java-kat-test");
        TenorGifs gifs = api.get("/search", "anime").get();

        Arrays.stream(gifs.results()).forEach(c -> System.out.println(c.getGif()));

    }

}
