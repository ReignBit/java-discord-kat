package com.reign.kat.commands.level;

import com.reign.api.kat.models.ApiMemberData;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.MessageContext;
import com.reign.kat.lib.converters.UserOrAuthorConverter;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import com.reign.kat.lib.utils.KatColor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class LevelCommand extends Command {
    public LevelCommand() {
        super(new String[]{"level"},"level" ,"Check a user's level");
        addConverter(new UserOrAuthorConverter(
                "user",
                "ID of the user to get level stats for.",
                null
        ));
    }

    @Override
    public void execute(Context ctx, CommandParameters args) {
        User target = args.get("user");

        ApiMemberData data = ApiMemberData.get(ctx.guild.getId(), target.getId());


        ctx.send(generateLevelEmbed(ctx, data));

    }

    private MessageEmbed generateLevelEmbed(Context ctx, ApiMemberData data)
    {
        return new GenericEmbedBuilder()
                .setAuthor(
                        String.format("%s's Level", ctx.author.getEffectiveName()),
                        null,
                        ctx.author.getEffectiveAvatarUrl()
                )
                .setColor(KatColor.LEVEL_CATEGORY)
                .setDescription(
                        String.format("**Level** `%d`\n`%dxp/???`", data.level.level(), data.level.xp)
                )
                .build();
    }
}
