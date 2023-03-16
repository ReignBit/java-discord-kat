package com.reign.kat;

import com.reign.kat.lib.utils.Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotVersion
{
    private static String number;
    private static String builtAt;
    private static String hash;

    public static String version()
    {
        return number;
    }

    public static String builtAt()
    {
        return builtAt;
    }

    public static String hash()
    {
        return hash;
    }

    public static void load() throws IOException
    {

        Properties version = new Properties();
        InputStream versioning = Utilities.class.getResourceAsStream("/version.properties");
        version.load(versioning);

        number = version.getProperty("version");
        builtAt = version.getProperty("timestamp");
        hash = version.getProperty("commit");
    }

}