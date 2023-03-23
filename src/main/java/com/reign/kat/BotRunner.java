package com.reign.kat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class BotRunner {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Bot bot;

    public static class BotProperties
    {
        public static boolean updateSlashCommands = false;
        public static boolean exitAfterLaunch = false;
    }

    public BotRunner(String[] args) {

        for (String arg :
                args)
        {
            if ("-UpdateSlashCommands".equals(arg))
            {
                BotProperties.updateSlashCommands = true;
                break;
            }

            if("-ExitAfterLaunch".equals(arg))
            {
                BotProperties.exitAfterLaunch = true;
                break;
            }
        }
    }

    public void run(){
        try {
            Bot bot = new Bot();
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    public void restart(){

    }
}
