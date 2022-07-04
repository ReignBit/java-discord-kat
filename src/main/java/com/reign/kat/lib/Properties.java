package com.reign.kat.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Scanner;

import com.reign.kat.lib.exceptions.PropertiesException;

public class Properties {
    private static final Logger log = LoggerFactory.getLogger(Properties.class);

    private String token;
    private String prefix = "!";
    private String backend_api_key;
    private String backend_api_host;

    private String tenorApiKey;

    public String getToken() { return token; }
    public String getPrefix() { return prefix; }
    public String getBackendApiKey() { return backend_api_key; }
    public String getBackendApiHost() { return backend_api_host; }
    public String getTenorApiKey() { return tenorApiKey; }

    private File propertiesFile;

    public Properties() throws Exception{
        log.debug("Reading config file");
        readPropertiesFile();
        requiredValuesSet();
        
    }
    /* TODO: Maybe change this to a Class based config?
        instead of a switch case, maybe have some kind of Config model that is loaded
     */
    private void readPropertiesFile() throws Exception{
        this.propertiesFile = new File("config.properties");
        if(!this.propertiesFile.exists()){
            Exception e = new PropertiesException("config.properties file missing");
            log.error("config.properties file missing");
            throw e;
        }
        Scanner fscan = new Scanner(this.propertiesFile);
        while(fscan.hasNext()){
            String line = fscan.nextLine();
            if(line.equals("\n"))
                continue;
            line = line.replace("= ","=");
            line = line.replace(" =","=");
            String[] args = line.split("=");
            if(args.length > 2){
                for(int i = 2; i < args.length; i++){
                    args[1] = args[1] + "=" + args[i];
                }
            }
            switch (args[0]){
                case "bot_token":
                    this.token = args[1];
                    break;
                case "prefix":
                    this.prefix = args[1];
                    break;
                case "tenor_api_key":
                    this.tenorApiKey = args[1];
                    break;
                case "backend_api_key":
                    this.backend_api_key = args[1];
                case "backend_api_host":
                    this.backend_api_host = args[1];
                default:
                    break;
            }
        }
    }

    public void requiredValuesSet() throws Exception{
        if(this.token == null)
            throw new PropertiesException("Bot token not set");
        if(this.tenorApiKey == null)
            throw new PropertiesException("Tenot API token not set");
    }

    @Override
    public String toString() {
        return "{" +
            " token='" + getToken() + "'" +
            ", defaultPrefix='" + getPrefix() + "'" +
            ", tenorApiKey='" + getTenorApiKey() + "'" +
            "}";
    }

    // @Override
    // public String toString()
    // {
    //     return String.format("Token: %s*****, DefaultPrefix: %s", token.substring(0, 10), defaultPrefix);
    // }

}
