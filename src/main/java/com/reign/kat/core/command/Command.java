package com.reign.kat.core.command;

import com.reign.kat.core.converters.Converter;

import java.util.ArrayList;

public abstract class Command {


    public String name = this.getClass().getCanonicalName();
    private String[] aliases;
    private String description;
    public ArrayList<Converter<?>> converters = new ArrayList<>();

    public Command(String[] aliases, String description)
    {
        this.aliases = aliases;
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
    public String[] getAliases() { return aliases; }
    public String getPrimaryAlias() { return aliases[0]; }
    public String getDescription() { return description; }

    public int getRequiredCount() { return converters.stream().filter(converter -> !converter.optional).toList().size();}
    public String name() { return aliases[0]; }

    public abstract void execute(Context ctx, CommandParameters args);
}
