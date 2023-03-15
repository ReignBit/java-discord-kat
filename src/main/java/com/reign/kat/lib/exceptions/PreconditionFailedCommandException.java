package com.reign.kat.lib.exceptions;

public class PreconditionFailedCommandException extends CommandException
{
    public PreconditionFailedCommandException(String error)
    {
        super(":octagonal_sign:", "Failed to run command", error);
    }
}
