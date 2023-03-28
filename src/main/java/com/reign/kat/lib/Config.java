package com.reign.kat.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.Scanner;

import com.reign.kat.lib.exceptions.PropertiesException;

//public class Properties {
//    private static final Logger log = LoggerFactory.getLogger(Properties.class);
//
//    private String token;
//    private String prefix = "!";
//    private String backend_api_key;
//    private String backend_api_host;
//
//    private boolean isDebug;
//    private boolean ignorePermissions;
//
//    private String tenorApiKey;
//
//    private int voice_autodisconnect_mins = 5;
//
//    public String getToken() { return token; }
//    public String getPrefix() { return prefix; }
//    public String getBackendApiKey() { return backend_api_key; }
//    public String getBackendApiHost() { return backend_api_host; }
//    public String getTenorApiKey() { return tenorApiKey; }
//
//    public int getVoiceAutoDisconnectMinutes() { return voice_autodisconnect_mins; }
//
//    public boolean isDebug() { return isDebug; }
//    public boolean isIgnorePermissions() { return ignorePermissions; }
//
//    public Properties() throws Exception{
//        log.debug("Reading config file");
//        readPropertiesFile();
//        requiredValuesSet();
//
//    }
//    /* TODO: Maybe change this to a Class based config?
//        instead of a switch case, maybe have some kind of Config model that is loaded
//     */
//    private void readPropertiesFile() throws Exception{
//        File propertiesFile = new File("config.properties");
//        if(!propertiesFile.exists()){
//            Exception e = new PropertiesException("config.properties file missing");
//            log.error("config.properties file missing");
//            throw e;
//        }
//        Scanner fscan = new Scanner(propertiesFile);
//        while(fscan.hasNext()){
//            String line = fscan.nextLine();
//            if(line.equals("\n"))
//                continue;
//            line = line.replace("= ","=");
//            line = line.replace(" =","=");
//            String[] args = line.split("=");
//            if(args.length > 2){
//                for(int i = 2; i < args.length; i++){
//                    args[1] = args[1] + "=" + args[i];
//                }
//            }
//            switch (args[0]){
//                case "bot_token":
//                    this.token = args[1];
//                    break;
//                case "prefix":
//                    this.prefix = args[1];
//                    break;
//                case "tenor_api_key":
//                    this.tenorApiKey = args[1];
//                    break;
//                case "backend_api_key":
//                    this.backend_api_key = args[1];
//                    break;
//                case "backend_api_host":
//                    this.backend_api_host = args[1];
//                    break;
//                case "voice_autodisconnect_mins":
//                    this.voice_autodisconnect_mins = Integer.parseInt(args[1]);
//                    break;
//                case "debug-mode":
//                    this.isDebug = Boolean.parseBoolean(args[1]);
//                    break;
//                case "debug-ignore-permission-system":
//                    this.ignorePermissions = Boolean.parseBoolean(args[1]);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//
//    public void requiredValuesSet() throws Exception{
//        if(this.token == null)
//            throw new PropertiesException("Bot token not set");
//        if(this.tenorApiKey == null)
//            throw new PropertiesException("Tenot API token not set");
//    }
//
//    @Override
//    public String toString() {
//        return "{" +
//            " token='" + getToken() + "'" +
//            ", defaultPrefix='" + getPrefix() + "'" +
//            ", tenorApiKey='" + getTenorApiKey() + "'" +
//            "}";
//    }
//
//    // @Override
//    // public String toString()
//    // {
//    //     return String.format("Token: %s*****, DefaultPrefix: %s", token.substring(0, 10), defaultPrefix);
//    // }
//
//}


public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    public static String BOT_TOKEN = "<BOT TOKEN HERE>";
    public static String PREFIX = "!";
    public static String BACKEND_API_KEY = "<BACKEND API KEY>";
    public static String BACKEND_API_HOST = "http://localhost";

    public static String TENOR_API_KEY = "<TENOR API KEY>";

    public static int VOICE_AUTODISCONNECT_MINUTES = 5;
    public static String VOICE_SPOTIFY_CLIENT_ID = "";
    public static String VOICE_SPOTIFY_CLIENT_SECRET = "";
    public static String VOICE_SPOTIFY_COUNTRY_CODE = "GB";

    public static boolean DEBUG_MODE;
    public static boolean DEBUG_IGNORE_PERMISSION_SYS;

    public static String SPEECH_RECOGNITION_MODEL_NAME = "";
    public static String[] SPEECH_RECOGNITION_WAKE_WORDS = new String[]{};
    public static String SPEECH_RECOGNITION_WAKE_WORD = "";

    public static boolean load()
    {
        try
        {
            Properties config = new Properties();
            FileInputStream fis = new FileInputStream("config.properties");

            try (fis)
            {
                config.load(fis);
                BOT_TOKEN = config.getProperty("bot-token");
                PREFIX = config.getProperty("prefix");
                TENOR_API_KEY = config.getProperty("tenor-api-key");
                BACKEND_API_HOST = config.getProperty("backend-api-host");
                BACKEND_API_KEY = config.getProperty("backend-api-key");

                VOICE_AUTODISCONNECT_MINUTES = Integer.parseInt(
                        config.getProperty("voice-autodisconnect-minutes")
                );
                VOICE_SPOTIFY_CLIENT_ID = config.getProperty("voice-spotify-client-id");
                VOICE_SPOTIFY_CLIENT_SECRET = config.getProperty("voice-spotify-client-secret");
                VOICE_SPOTIFY_COUNTRY_CODE = config.getProperty("voice-spotify-country-code");



                DEBUG_MODE = Boolean.parseBoolean(config.getProperty("debug-mode"));
                DEBUG_IGNORE_PERMISSION_SYS = Boolean.parseBoolean(config.getProperty("debug-ignore-permission-sys"));

                SPEECH_RECOGNITION_MODEL_NAME = config.getProperty("speech-recognition-model-name");
                SPEECH_RECOGNITION_WAKE_WORDS = config.getProperty("speech-recognition-wake-words").split(", ");
                SPEECH_RECOGNITION_WAKE_WORD = SPEECH_RECOGNITION_WAKE_WORDS[0];
                return true;
            } catch (Exception e)
            {
                return false;
            }
        } catch (FileNotFoundException e)
        {
            log.error("config.properties file doesn't exist.");

            createDefaultConfig();
            throw new RuntimeException(e);
        }
    }

    private static void createDefaultConfig()
    {
        try
        {
            FileWriter fw = new FileWriter("config.properties");
            // Core bot stuff
            fw.write("bot-token=" + BOT_TOKEN + "\n");
            fw.write("prefix=" + BOT_TOKEN + "\n");
            fw.write("backend-api-key=" + BACKEND_API_KEY + "\n");
            fw.write("backend-api-host=" + BACKEND_API_HOST + "\n");

            // Misc.
            fw.write("tenor-api-key=" + TENOR_API_KEY + "\n");

            // Voice specific
            fw.write("voice-autodisconnect-minutes=" + VOICE_AUTODISCONNECT_MINUTES + "\n");
            fw.write("voice-spotify-client-id=" + VOICE_SPOTIFY_CLIENT_ID + "\n");
            fw.write("voice-spotify-client-secret=" + VOICE_SPOTIFY_CLIENT_SECRET + "\n");
            fw.write("voice-spotify-country-code=" + VOICE_SPOTIFY_COUNTRY_CODE + "\n");

            // Debug
            fw.write("debug-mode=" + DEBUG_MODE + "\n");
            fw.write("debug-ignore-permission-sys=" + DEBUG_IGNORE_PERMISSION_SYS + "\n");
            fw.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}