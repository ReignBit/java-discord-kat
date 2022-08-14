package com.reign.kat.lib.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.converters.Converter;
import com.reign.kat.lib.exceptions.MissingArgumentCommandException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Converts a command's arguments into objects, decided by Command.converters
 */
public class CommandParameters {
    private static final Logger log = LoggerFactory.getLogger(CommandParameters.class);

    private final MessageReceivedEvent event;
    private final SmartStringSplitter scanner;

    public HashMap<String, Converter<?>> params = new HashMap<>();

    public CommandParameters(MessageReceivedEvent event, String commandline) {
        this.event = event;
        this.scanner = new SmartStringSplitter(commandline);


    }

    /**
     * Takes an array of Strings and attempts to run a command's list of Converters on them.
     *
     * @param command Command in which to try to convert for.
     * @throws MissingArgumentCommandException The input strArgs has missing arguments for the specified command.
     */
    public void parse(Command command) throws MissingArgumentCommandException {
        if (scanner.size() < command.getRequiredCount()) {
            throw new MissingArgumentCommandException("Missing required arguments");
        }

        for (int i = 0; i < command.converters.size(); i++) {
//            String s = i < strArgs.size() ? strArgs.get(i) : null;
            log.debug(String.valueOf(command.converters.get(i).isGreedy));
            String s = command.converters.get(i).isGreedy ? scanner.all() : scanner.next();

            Converter<?> converter = command.converters.get(i).convert(s, event);

            if (converter.optional && converter.get() == null)
            {
                converter.setDefault();
            }

            params.put(converter.argName, converter);
        }
    }

    @Deprecated
    public <T> T get(int index) {
        if (params.isEmpty() || index >= params.size()) {
            return null;
        }
        // Since we are trying to get by index, convert HashMap to a List of Converter<?> and get the item value.
        return params.values().stream().toList().get(index).get();
    }

    public <T> T get(String key)
    {
        if (!params.containsKey(key))
        {
            return null;
        }
        return params.get(key).get();
    }
}
