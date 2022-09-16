package com.reign.kat.lib.utils;

import com.reign.kat.lib.voice.GuildAudioManager;

import java.io.File;
import java.util.*;

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

    public static String timeConversion(Long millie) {
        if (millie != null) {
            long seconds = (millie / 1000);
            long sec = seconds % 60;
            long min = (seconds / 60) % 60;
            long hrs = (seconds / (60 * 60)) % 24;
            if (hrs > 0) {
                return String.format("%02d:%02d:%02d", hrs, min, sec);
            } else {
                return String.format("%02d:%02d", min, sec);
            }
        } else {
            return null;
        }
    }

    public static Long stringToTimeConversion(String timestamp) {
        List<String> times = new ArrayList<>(Arrays.stream(timestamp.split(":")).toList());
        times.sort(Collections.reverseOrder());

        // 0 => seconds, 1 => minutes

        if (times.size() == 0)
        {
            return -1L;
        }

        long seconds = Long.parseLong(times.get(0)) * 1000;

        long minutes = 0L;
        if (times.size() > 1)
        {
            minutes = Long.parseLong(times.get(1)) * 60000;
        }

        return seconds + minutes;

    }
}
