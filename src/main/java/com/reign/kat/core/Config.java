package com.reign.kat.core;

public class Config {
    private String token = "YOUR BOT TOKEN HERE";
    private String defaultPrefix = "!";

    public String getToken() { return token; }
    public String getDefaultPrefix() { return defaultPrefix; }

    @Override
    public String toString()
    {
        return String.format("Token: %s*****, DefaultPrefix: %s", token.substring(0, 10), defaultPrefix);
    }

}
