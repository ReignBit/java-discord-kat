package com.reign.kat.commands.lib;

import com.reign.kat.Bot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Cog extends CommandHandler{
    private static final Logger log = LogManager.getLogger(Cog.class);

    public Cog(DiscordApi api)
    {
        api.addMessageCreateListener(event -> handleMessageCreate(api, event));
    }

    void handleMessageCreate(DiscordApi api, MessageCreateEvent event)
    {
        Message message = event.getMessage();
        /*
        1. Cancel execution if the message is from a bot or our self.
        2. Split the message into the prefix, command, args
        3. Find the command from our commands HashMap.
        4. Invoke the method responsible for the command along with the args.
         */

        if (message.getAuthor().isBotUser() || !message.getContent().startsWith(getServerPrefix(event.getServer().get().getIdAsString())))
        {
            return;
        }

        String[] splitMsg = message.getContent().split(" ");

        // Remove the prefix from the command string.
        String cmdString = splitMsg[0].substring(getServerPrefix(event.getServer().orElse(null).getIdAsString()).length());

        // Get the CommandInfo for the commandString.
        CommandInfo command = commands.get(cmdString);
        if (command == null) { return; }

        Command cmdAnnotation = command.getAnnotation();
        // Future annotation stuff here

        // Collect args.
        final Object[] params = getParameters(splitMsg, command, event, api);
        invokeMethod(command, message, params);


    }

    private void invokeMethod(CommandInfo command, Message message, Object[] params)
    {
        Method method = command.getMethod();
        try {
            method.setAccessible(true);
            log.info("COMMAND {} ", command.getExecutor().toString());
            method.invoke(command.getExecutor(), params);
        } catch (Exception e)
        {
            log.warn("Exception occurred whilst executing command {}", command.getExecutor().toString(), e);
        }

    }

    private Object[] getParameters(String[] splitMessage, CommandInfo command, MessageCreateEvent event, DiscordApi api)
    {
        Message message = event.getMessage();
        String[] args = Arrays.copyOfRange(splitMessage, 1, splitMessage.length);
        Class<?>[] parameterTypes = command.getMethod().getParameterTypes();
        final Object[] parameters = new Object[parameterTypes.length];
        int stringCounter = 0;
        for (int i = 0; i < parameterTypes.length; i++) { // check all parameters
            Class<?> type = parameterTypes[i];
            if (type == String.class) {
                if (stringCounter++ == 0) {
                    parameters[i] = splitMessage[0]; // the first split is the command
                } else {
                    if (args.length + 2 > stringCounter) {
                        // the first string parameter is the command, the other ones are the arguments
                        parameters[i] = args[stringCounter - 2];
                    }
                }
            } else if (type == String[].class) {
                parameters[i] = args;
            } else if (type == MessageCreateEvent.class) {
                parameters[i] = event;
            } else if (type == Message.class) {
                parameters[i] = message;
            } else if (type == DiscordApi.class) {
                parameters[i] = api;
            } else if (type == Channel.class) {
                parameters[i] = message.getChannel();
            } else if (type == PrivateChannel.class) {
                parameters[i] = message.getChannel().asPrivateChannel().orElse(null);
            } else if (type == ServerChannel.class) {
                parameters[i] = message.getChannel().asServerChannel().orElse(null);
            } else if (type == ServerTextChannel.class) {
                parameters[i] = message.getChannel().asServerTextChannel().orElse(null);
            } else if (type == TextChannel.class) {
                parameters[i] = message.getChannel().asTextChannel().orElse(null);
            } else if (type == User.class) {
                parameters[i] = message.getUserAuthor().orElse(null);
            } else if (type == MessageAuthor.class) {
                parameters[i] = message.getAuthor();
            }  else if (type == Server.class) {
                parameters[i] = message.getServerTextChannel().map(ServerTextChannel::getServer).orElse(null);
//            } else if (type == Object[].class) {
//                parameters[i] = getObjectsFromString(api, args);
            } else {
                // unknown type
                parameters[i] = null;
            }
        }
        return parameters;
    }

    String getServerPrefix(String serverId)
    {
        /* Here we would get API request to retrieve server prefix, for now we just get default */
        return Bot.getPrefix();
    }
}
