package com.reign.kat.lib.exceptions;

public class InsufficientPermissionsCommandException extends CommandException {
    public InsufficientPermissionsCommandException(String error)
    {
        super(":no_entry:", "Insufficient Permissions", error);
    }
}
