package com.reign.kat.core;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
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

    Config buildConfigFromFile(String filename)
    {
        File file = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        try
        {
            return objectMapper.readValue(file, Config.class);
        }
        catch(StreamReadException | DatabindException err)
        {
            log.error("Failed to parse YAML in {}", filename);
            return null;
        }
        catch(IOException err)
        {
            log.error("Failed to read {}", filename);
            return null;
        }

    }

    Config writeDefaultConfigToFile(String fileName)
    {
        log.error("config file not found, creating default");
        File file = new File(fileName);
        Config defaultConfig = new Config();
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));

        try
        {
            objectMapper.writeValue(file, defaultConfig);
            return defaultConfig;
        } catch (IOException err)
        {
            // Failed to create default config. Let's just stop now.
            log.error("Failed to create default config file at {}. {}", fileName, err);
            return null;
        }
    }

    public ConfigBuilder(String fileName) {
        log.info("Building Config...");
        long now = Instant.now().toEpochMilli();

        config = buildConfigFromFile(fileName);

        if (config == null)
        {
            // Failed to create Config. Let's check if the file exists
            File file = new File(fileName);
            if (file.exists())
            {
                log.error(
                        "Unable to read and parse {}. Check the file permissions and ensure the contents are valid YAML",
                        fileName
                );
                System.exit(2);
            }

            // Let's try to create default config file.
            config = writeDefaultConfigToFile(fileName);
            if (config == null)
            {
                log.error("Failed to write default configuration file.");
                System.exit(3);
            }

        }
    }
}
