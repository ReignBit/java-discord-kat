package com.reign.kat.lib.utils;

public class PreCommandResult
{
    public boolean passed;
    public String message;

    public PreCommandResult(boolean passed, String message)
    {
        this.passed = passed;
        this.message = message;
    }
}
