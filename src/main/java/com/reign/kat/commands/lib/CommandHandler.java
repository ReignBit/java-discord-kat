package com.reign.kat.commands.lib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class CommandHandler {
    HashMap<String, CommandInfo> commands = new HashMap<String, CommandInfo>();
    ArrayList<CommandInfo> commandInfoList = new ArrayList<CommandInfo>();

    private static final Logger log = LogManager.getLogger(CommandHandler.class);

    public void registerCommand(CommandExecutor executor)
    {
        for (Method method: executor.getClass().getMethods()) {
            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null) { continue; }

            if (annotation.aliases().length == 0) {
                throw new IllegalArgumentException("Command must have at least one alias!");
            }

            CommandInfo command = new CommandInfo(annotation, method, executor);
            for (String alias : annotation.aliases()) {
                log.info("Registering command alias {}", alias.toLowerCase().replace(" ", ""));
                commands.put(alias.toLowerCase().replace(" ", ""), command);
            }
        }
    }

    public static class CommandInfo
    {
        private final Command annotation;
        private final Method method;
        private final CommandExecutor executor;

        CommandInfo(Command annotation, Method method, CommandExecutor executor)
        {
            this.annotation = annotation;
            this.method = method;
            this.executor = executor;
        }

        public Command getAnnotation() { return annotation; };
        public Method getMethod() { return method; };
        public CommandExecutor getExecutor() { return executor; };
    }
}


