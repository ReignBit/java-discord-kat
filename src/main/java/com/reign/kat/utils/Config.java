package com.reign.kat.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


public class Config {
    public static Properties getConfig(String filepath)
    {
        Properties prop = new Properties();
        try
        {
            InputStream inputStream = new FileInputStream(filepath);
            prop.load(inputStream);
        }
        catch(IOException ex)
        {
            // Failed to find/read file. Create a new one with defaults
            try
            {
                System.out.println(String.format("Failed to open file %s. Creating default properties...", filepath));
                prop.setProperty("token", "<your token here>");
                prop.setProperty("backend-host", "https://example.com/api");
                prop.setProperty("backend-auth-method", "Basic ");
                prop.setProperty("backend-auth-token", "<your auth token here>");
    
                OutputStream oStream = new FileOutputStream(filepath);
                prop.store(oStream, "");
            }
            catch(IOException ex2)
            {
                System.out.println("Failed to create default properties file at " + filepath);
                return null;
            }
        }
        return prop;
    }
}
