package com.reign.kat.core;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.Instant;


public class ConfigBuilder {
    private static final Logger log = LoggerFactory.getLogger(ConfigBuilder.class);

    private Config config;
    public Config getConfig() { return config; }

    public ConfigBuilder(String fileName) {
        log.info("Building Config...");
        long now = Instant.now().toEpochMilli();
        try
        {
            File file = new File(fileName);
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            config = objectMapper.readValue(file, Config.class);
            log.info("Built config in {}ms", Instant.now().toEpochMilli() - now);
            log.info(config.toString());

        } catch (IOException e)
        {
            log.error("Failed to read `{}` config file", fileName);
            System.exit(404);
        }

    }
}
