package com.reign.kat.lib.exceptions;

public class MissingArgumentCommandException extends CommandException {
    public MissingArgumentCommandException(String error)
    {
        super(":octagonal_sign:", "Missing argument(s)", error);
    }
}
