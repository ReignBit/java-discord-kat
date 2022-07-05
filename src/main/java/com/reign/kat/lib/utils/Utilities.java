package com.reign.kat.lib.utils;

import java.io.File;
import java.util.Scanner;

public class Utilities {

    public static String readVersion()
    {
        File file = new File("./version");
        try
        {
            Scanner scan = new Scanner(file);
            return scan.nextLine();
        }
        catch (Exception e)
        {
            return "unknown";
        }
    }
}
