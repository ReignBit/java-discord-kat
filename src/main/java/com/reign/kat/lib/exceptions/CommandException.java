package com.reign.kat.lib.exceptions;

public class CommandException extends Exception {

    public String emoji;
    public String title;
    public String err;

    public CommandException(String emoji, String title, String err)
    {
        super(err);
        this.emoji = emoji;
        this.title = title;
        this.err = err;

    }
}
