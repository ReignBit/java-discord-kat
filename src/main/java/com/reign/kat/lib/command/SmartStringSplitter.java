package com.reign.kat.lib.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class SmartStringSplitter {

    private static final Logger log = LoggerFactory.getLogger(SmartStringSplitter.class);
    private final String input;
    private final LinkedList<String> queue = new LinkedList<>();

    public SmartStringSplitter(String input)
    {
        this.input = input;

        build(input);


    }

    /**
     * Splits the input string by space and quote delimitation.
     * @param in String to split up.
     */
    private void build(String in)
    {
        StringBuilder sb = new StringBuilder();
        boolean isQuoted = false;
        for (Character s:
                input.toCharArray()) {

            if (s.equals('"'))
            {

                // != 0 means we were in the middle of processing a string. Let's complete
                // that one and make a new one

                if (isQuoted)
                {
                    sb.append(s);
                }
                isQuoted = !isQuoted;
                if (sb.length() != 0)
                {
                    queue.add(sb.toString());
                    sb.setLength(0);
                }
                else    // Already in the new String.
                {
                    sb.append(s);
                }
            }
            else if (s.equals(' ') && !isQuoted)
            {
                // break into new string
                queue.add(sb.toString());
                sb.setLength(0);
            }
            else
            {
                sb.append(s);
            }



        }
        if (sb.length() > 0)
        {
            queue.add(sb.toString());

        }
        log.trace("Built SmartStringSplitter:");
        log.trace("{} => ",input);
        for (String s:
                queue) {
            log.trace("  - {}",s);
        }
    }

    /**
     * Consumes and returns the next string available.
     * @return next string in the queue.
     */
    public String next()
    {
        if (!queue.isEmpty())
        {
            return queue.pop();
        }
        return null;
    }

    /**
     * Consumes and returns the rest of the available strings as one string.
     * @return String of all available strings in the queue.
     */
    public String all()
    {
        if (queue.isEmpty())
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        while(!queue.isEmpty())
        {
            sb.append(next()).append(" ");
        }
        return sb.toString().strip();
    }

    /**
     * Returns the size of the queue.
     */
    public int size()
    {
        return queue.size();
    }
}
