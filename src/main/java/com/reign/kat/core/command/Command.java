package com.reign.kat.core.command;

public abstract class Command {


    public String name = this.getClass().getCanonicalName();
    private String[] aliases = {};
    private String description = "A description would usually go here...";

    public Command(String[] aliases, String description)
    {
        this.aliases = aliases;
        this.description = description;
    }

    public String[] getAliases() { return aliases; }
    public String getPrimaryAlias() { return aliases[0]; }
    public String getDescription() { return description; }

    public abstract void execute(Context ctx);
}
