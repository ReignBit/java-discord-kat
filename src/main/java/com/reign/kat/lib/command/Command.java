package com.reign.kat.lib.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.reign.kat.lib.PermissionHandler;
import com.reign.kat.lib.converters.Converter;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.exceptions.PreconditionFailedCommandException;
import com.reign.kat.lib.utils.PermissionGroupType;
import com.reign.kat.lib.utils.PreCommandResult;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Command {
    protected static final Logger log = LoggerFactory.getLogger(Command.class);

    public String name = this.getClass().getCanonicalName();
    private final HashSet<String> aliases;
    private final String primaryAlias;
    private final String description;

    private boolean showTyping = false;

    protected PermissionGroupType requiredPermission = PermissionGroupType.EVERYONE;
    protected int requiredDiscordPermission = 0;


    public ArrayList<Converter<?>> converters = new ArrayList<>();
    public LinkedList<BiFunction<Context, CommandParameters, PreCommandResult>> precommands = new LinkedList<>();

    public Command(String[] aliases, String primaryAlias, String description)
    {
        this.aliases = new HashSet<>();
        this.aliases.addAll(Arrays.asList(aliases));
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

    public void addPreCommand(BiFunction<Context, CommandParameters, PreCommandResult> precommand)
    {
        precommands.add(precommand);
    }

    public CommandData updateSlashData()
    {
        SlashCommandData slashCmd = Commands.slash(primaryAlias, description);

        ArrayList<OptionData> options = new ArrayList<>();
        converters.forEach(converter -> options.add(converter.getSlashOptionData()));

        slashCmd.addOptions(options);
        return slashCmd;
    }

    public HashSet<String> getAliases() { return aliases; }
    public String getPrimaryAlias() { return primaryAlias; }
    public String getDescription() { return description; }
    public String getName(){return primaryAlias;}

    public void setShowTyping(boolean status) {this.showTyping = status; }

    public int getRequiredCount() { return converters.stream().filter(converter -> !converter.optional).toList().size();}

    /**
     * Returns the argument signature for the command.
     * <br>For example, the `command !kick UserID opt:reason`
     * <br>would build a signature string of:- <br><code>kick &#60;user:User&#62; [reason: String]</code>
     * @return String signature
     */
    public String getSignature()
    {
        StringBuilder sb = new StringBuilder().append(getPrimaryAlias()).append(" ");

        for (Converter<?> arg: converters)
        {
            if (arg.optional)
            {
                sb.append("[").append(arg.argName).append(" : ").append(arg.getType()).append("] ");
            }
            else
            {
                sb.append("<").append(arg.argName).append(" : ").append(arg.getType()).append("> ");
            }
        }
        return sb.toString();
    }

    /**
     * Set the required permission type a user must have to execute the command.
     * @param permission PermissionGroupType to limit execution to (and above).
     */
    public void setRequiredPermissionGroup(PermissionGroupType permission)
    {
        requiredPermission = permission;
    }

    /**
     * Set the required Discord permissions a user must have to execute the command.
     * Examples: <code>MANAGE_MESSAGES, ADMINISTRATOR`</code>.<br>
     * It may be better to use setRequiredPermissionGroup instead, unless the command action would require
     * a Discord permission - a command to delete messages, for example.
     * @param permBitfield DiscordPermission bitfield of permissions.
     */
    public void setRequiredDiscordPermission(int permBitfield)
    {
        requiredDiscordPermission = permBitfield;
    }

    public boolean isPrivileged(Member member, GuildChannel channel)
    {
        return PermissionHandler.isPrivileged(member, channel, requiredDiscordPermission, requiredPermission);
    }

    public abstract void execute(Context ctx, CommandParameters args) throws Exception;


    public void invokeCommand(Context c, CommandParameters args) throws Exception
    {
        Method executeMethod = Arrays.stream(getClass().getDeclaredMethods()).filter(method -> method.getName().equals("execute")).toList().get(0);


        for (BiFunction<Context, CommandParameters, PreCommandResult> func :
                precommands)
        {
            PreCommandResult result = func.apply(c, args);
            if (result != null && !result.passed)
            {
                // false means that the pre-command has failed its checks. We should exit command execution here
                // and send the message returned instead.

                c.sendEmbeds(new ExceptionEmbed()
//                        .setTitle("Failed to run command " + c.command.getPrimaryAlias())
                        .setDescription(result.message).build());

                return;
            }
        }


        c.channel.sendTyping().queue();
        execute(c, args);

    }

}
