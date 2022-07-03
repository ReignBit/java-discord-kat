package com.reign.kat;

import com.reign.kat.commands.debug.DebugCategory;
import com.reign.kat.commands.fun.emote.EmoteCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
    {
        log.info("Starting Kat v{}", Bot.getVersion());


        Bot bot = new Bot("./config.yaml");
        bot.addCategory(new DebugCategory());
        bot.addCategory(new EmoteCategory());
    }
}
