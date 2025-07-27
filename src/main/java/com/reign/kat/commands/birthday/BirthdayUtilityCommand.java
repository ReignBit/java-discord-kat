package com.reign.kat.commands.birthday;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class BirthdayUtilityCommand extends Command {

    public BirthdayUtilityCommand() {
        super(new String[]{"config"}, "config", "Birthday configuration");
        /*
            !birthday config <tag> <value>
         */
        //TODO: Add a Converter which converts from a list of valid inputs
        addConverter(new StringConverter("key", "[enabled/announce]", null));
        addConverter(new StringConverter("value", "[enabled: true/false, announce: channelID]", null));

    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());

        String key = args.get("key");
        String value = args.get("value");

        if (key.equalsIgnoreCase("enabled")) {
            switch (value.toLowerCase()) {
                case "true", "false":
                    category.datastore.updateField(BirthdayCategory.DK_FEATURE_ENABLED, value.equalsIgnoreCase("true"), guild);
                    ctx.send(new GenericEmbedBuilder()
                            .setTitle("Birthday Announcements")
                            .setDescription("Birthday announcements have been" + (value.equalsIgnoreCase("true") ? "enabled" : "disabled" ))
                            .build());
                    break;
                default:
                    ctx.send(new ExceptionEmbed()
                            .setDescription("`" + value + "` is an invalid option for the selected property!")
                            .build());
            }
            return;
        }

        if (key.equalsIgnoreCase("announce")) {
            TextChannel channel = ctx.guild.getTextChannelById(value);
            if (channel != null) {
                category.datastore.updateField(BirthdayCategory.DK_ANNOUNCE_ID, value, guild);
                ctx.send(new GenericEmbedBuilder()
                        .setTitle("Birthday Channel Set")
                        .setDescription(
                                String.format("I will announce birthdays to `%s`", channel.getName()))
                        .build());
                return;
            }

            ctx.send(new ExceptionEmbed().setDescription("Invalid channel ID").build());
            return;
        }

        ctx.send(new ExceptionEmbed().setDescription("Invalid property!\nAvailable:\n`enabled`\n`announce`").build());
    }
}
