package com.reign.kat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotRunner {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private Bot bot;

    public BotRunner() {
    }

    public void run(){
        try {
            Bot bot = new Bot();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public void restart(){

    }
}
