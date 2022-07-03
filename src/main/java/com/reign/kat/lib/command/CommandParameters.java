package com.reign.kat.lib.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.reign.kat.lib.converters.Converter;
import com.reign.kat.lib.exceptions.MissingArgumentException;

import java.util.ArrayList;

/**
 * Converts a command's arguments into objects, decided by Command.converters
 */
public class CommandParameters {
    private static final Logger log = LoggerFactory.getLogger(CommandParameters.class);

    private final MessageReceivedEvent event;

    public ArrayList<Converter<?>> params = new ArrayList<>();

    public CommandParameters(MessageReceivedEvent event) {
        this.event = event;
    }

    /**
     * Takes an array of Strings and attempts to run a command's list of Converters on them.
     *
     * @param strArgs String[] command given by the user.
     * @param command Command in which to try to convert for.
     * @throws MissingArgumentException The input strArgs has missing arguments for the specified command.
     */
    public void parse(ArrayList<String> strArgs, Command command) throws MissingArgumentException {
        if (strArgs.size() < command.getRequiredCount()) {
            throw new MissingArgumentException("Missing required arguments");
        }

        for (int i = 0; i < command.converters.size(); i++) {
            String s = i < strArgs.size() ? strArgs.get(i) : null;
            params.add(command.converters.get(i).convert(s, event));
        }
    }

    public <T> T get(int index) {
        if (params.isEmpty() || index > params.size()) {
            return null;
        }
        return params.get(index).get();
    }
}
