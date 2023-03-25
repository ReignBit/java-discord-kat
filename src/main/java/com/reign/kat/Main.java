package com.reign.kat;

import com.reign.kat.commands.debug.DebugCategory;
import com.reign.kat.commands.fun.emote.EmoteCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
    {

        try { BotVersion.load(); } catch (IOException e) { log.error("Exception whilst loading BotVersion, ", e); }

        if (Arrays.asList(args).contains("--version"))
        {
            System.out.println(BotVersion.version());
            System.exit(0);
        }



        log.info("Creating runner environment");
        BotRunner br = new BotRunner(args);
        log.info("Running");
        br.run();
    }
}
