package com.reign.kat.lib.exceptions;

public class MissingArgumentException extends Exception{
    public MissingArgumentException(String error)
    {
        super(error);
    }
}
