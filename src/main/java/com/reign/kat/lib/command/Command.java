package com.reign.kat.lib.command;

import java.util.ArrayList;
import java.util.HashSet;

import com.reign.kat.lib.converters.Converter;

public abstract class Command {


    public String name = this.getClass().getCanonicalName();
    private HashSet<String> aliases;
    private String primaryAlias;
    private String description;
    public ArrayList<Converter<?>> converters = new ArrayList<>();

    public Command(String[] aliases, String primaryAlias, String description)
    {
        this.aliases = new HashSet<>();
        for(String s : aliases){
            this.aliases.add(s);
        }
        this.primaryAlias = primaryAlias;
        this.description = description;
    }

    public void addConverter(Converter<?> converter)
    {
        if (converters.size() > 0)
        {
            Converter<?> lastConverter = converters.get(converters.size()-1);
            if (lastConverter.optional && !converter.optional)
            {
                throw new IllegalStateException("Optional arguments cannot precede required arguments.");
            }
        }

        this.converters.add(converter);
    }
    public HashSet<String> getAliases() { return aliases; }
    public String getPrimaryAlias() { return primaryAlias; }
    public String getDescription() { return description; }
    public String getName(){return primaryAlias;}

    public int getRequiredCount() { return converters.stream().filter(converter -> !converter.optional).toList().size();}

    public abstract void execute(Context ctx, CommandParameters args);
}
