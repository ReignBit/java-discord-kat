package com.reign.kat.lib.utils.stats;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;

public class CommandExecution {

    public Command command;
    public float timeTaken;
    public CommandParameters input;

    public CommandExecution(Command cmd, float timeTaken, CommandParameters input)
    {
        this.command = cmd;
        this.timeTaken = timeTaken;
        this.input = input;

    }
}
