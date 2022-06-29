package com.reign.kat.commands.lib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * Gets strings the command should execute to.
     * @return Command strings the executor should listen for.
     */
    String[] aliases();

    /**
     * Gets the description of the command.
     * @return The description of the command.
     */
    String description() default "";

    /**
     * Gets the usage of the command. Defaults to first alias.
     * @return The usage string of the command.
     */
    String usage() default "";

    /**
     * Gets the required permissions for the command to execute.
     * @return The permissions required.
     */
    String requiredPermissions() default "";

}
