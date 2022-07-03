# Command Framework

The command framework for the bot has 3 base classes:
- Command
- Category
- Converter

and 2 control classes:
- CommandHandler
- CommandParameters

## Command
This is the base command for any commands in the bot. Used for the main logic of any commands. The main entrypoint for commands is `execute(Context ctx, CommandParameters params)`.

Example command layout

```java
import com.reign.kat.core.command.Command;
import com.reign.kat.core.command.CommandParameters;
import com.reign.kat.core.command.Context;
import com.reign.kat.core.converters.MemberConverter;

public class ExampleCommand extends Command {
    public ExampleCommand()
    {
        // In the constructor we setup the aliases for the command, and its description.
        super(new String[] {"avatar"}, "Sends the targeted user's avatar to chat");

        // In the constructor, we also add our arguments. 
        // As of writing this there are currently the following converters:
        // - UserConverter    (Converts ID/Mention to a User Object)
        // - MemberConverter  (Converts ID/Mention to a Member Object)
        // - StringConverter  (Passes string arg to the command)
        //
        // Converters take the following parameters:
        // - String argName  The name of the argument
        // - String description  The description of the argument
        // - boolean optional  Is this argument required
        //
        // Since this Command will take a target Member's Avatar, we will need the command to have access
        // to the Member/User. Either would work in this case, but let's use MemberConverter
        addConverter(new MemberConverter());
        
    }

    // This is the entrypoint for the command.
    // Context - An object which has often used attributes (message, author, channel, etc)
    // CommandParameters - Contains
    @Override
    public void execute(Context ctx, CommandParameters args) {
        // In order to access the targeted member from our converter, we use the following.
        // CommandParameters.get(int index) - index being the position of the arg we want.
        // TODO: Maybe we could get via the argName?
        Member member = args.get(0);
        
        // Then its usual command logic
        ctx.channel.sendMessage(member.getAvatarURL()).queue();
    }
}
```

## Category
Categories are used to organize Commands. They are also responsible for registering and unregistering commands added to them.

Example Category that contains the above command:

```java
import com.reign.kat.core.command.category.Category;

// Extends from the base category
public class ExampleCategory extends Category {
    public ExampleCategory()
    {
        // In the constructor we can set the emoji used in the help command.
        // This can either be discord emojis (like below) or Unicode emojis
        setEmoji(":wave:");
        
        // Here we also register any commands the category is responsible for.
        registerCommand(new ExampleCommand());
    }
}
```

## Converter
Converters convert the string command argument (from discord) into an Object.

Example Converter that converts a Channel id into a TextChannel:

```java
import com.reign.kat.core.converters.Converter;
import net.dv8tion.jda.api.entities.TextChannel;

public class TextChannelConverter extends Converter<TextChannel> {
    
    // Constructor simply calls super for the passed parameters, setting the argument name, description and if optional.
    public MemberConverter(String argName, String description, boolean optional)
    {
        super(argName, description, optional);
    }

    // This is the entry point for the converter.
    // Supplied is the raw string from the message, in this case the supplied command was:
    // $delete #test-channel
    // The converter in this case is supplied with the first (and only) argument of this command:
    // #test-channel
    // (Note that the converter receives the Message.getContentRaw(), so the command actually
    // looks like:
    // $delete <#12321342115214>
    //
    // String toConvert  The raw argument
    // MessageReceivedEvent event  The event from the message. Can be used to get guild specific attributes for example.
    @Override
    public Converter<Member> convert(String toConvert, MessageReceivedEvent event) throws IllegalArgumentException{
        if (toConvert == null) {set(null); return; }        // If toConvert is null, then the user did not specific this argument
        
        if (toConvert.length() == 18)                       // The length of a discord snowflake
        {
            // The user input an ID?
            TextChannel channel = event.getGuild().getTextChannelById(toConvert);
        }
        else if (toConvert.startsWith("<#") && toConvert.endsWith(">"))
        {
            // The user mentioned the TextChannel?
            TextChannel channel = event.getGuild().getTextChannelById(toConvert.substring(2,toConvert.length()-2));
        }
        else
        {
            // The user did not enter anything that can be converted into a TextChannel
            throw new IllegalArgumentException(String.format("Could not convert %s into a TextChannel!", toConvert));
        }

        if (channel != null)
        {
            // the set method assigns this converter's `item` attribute, allowing it to be used in a command
            // when you use params.get(x)
            set(channel);
        }
    }
}
```

## CommandHandler
`Bot.commandHandler` is responsible for dispatching message commands to each of the command categories which then triggers the command.


## CommandParameters
This is created during the pre-parse phase of a command. Once the CommandHandler has found the correct command,
this is created and setup with the commands Converters - ready to be converted.