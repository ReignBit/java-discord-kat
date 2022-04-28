package com.reign.kat;

import java.util.Properties;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import com.reign.kat.utils.Config;


public class Bot {

    Properties config;
    
    public Bot()
    {
        config = Config.getConfig("resources/config/conf.properties");
        System.out.println(config);

        DiscordApi api = new DiscordApiBuilder()
                .setToken(config.getProperty("token"))
                .login().join();
    }
}
