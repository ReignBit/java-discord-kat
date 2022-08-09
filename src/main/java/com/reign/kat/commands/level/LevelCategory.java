package com.reign.kat.commands.level;


import com.reign.api.kat.models.ApiGuildData;
import com.reign.api.kat.models.ApiMemberData;
import com.reign.kat.Bot;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import com.reign.kat.lib.utils.KatColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class LevelCategory extends Category {

    private static final Logger log = LoggerFactory.getLogger(LevelCategory.class);

    public LevelCategory()
    {
        setEmoji(":sparkle:");
        registerCommand(new LevelCommand());
    }

    @Override
    public void onMessageReceivedNotBot(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        ApiGuildData guild = ApiGuildData.get(message.getGuild().getId());
        if (guild.level.enabled)
        {
            if (isMeaningfulMessage(message))
            {
                processLevel(message, guild);
            }
        }
    }

    private boolean isMeaningfulMessage(Message message)
    {
        return message.getContentStripped().length() > 10 &&
                !message.getContentStripped().startsWith("!") &&
                !message.getContentStripped().startsWith("$") &&
                !message.getContentStripped().startsWith("%") &&
                !message.getContentStripped().startsWith(".");
    }

    private void processLevel(Message message, ApiGuildData guild)
    {
        if (Bot.properties.isDebug())
        {
            if (!message.getChannel().getId().equals("502238372299931677")) { return; }
        }

        // Actual user, lets do level stuff.

        ApiMemberData member = ApiMemberData.get(message.getGuild().getId(), message.getAuthor().getId());

        if (giveExperience(message, guild, member))
        {
            message.replyEmbeds(generateLevelUpMessage(member)).delay(10, TimeUnit.SECONDS).flatMap(Message::delete).queue();
        }
    }

    private boolean giveExperience(Message message, ApiGuildData guild, ApiMemberData member)
    {
        int oldLevel = member.level.level();

        int xp = member.level.addExperience(message.getContentStripped(), guild.level.xpMultiplier);
        log.info("Awarded {} xp to {}|{}", xp, member.guildSnowflake, member.snowflake);
        member.save();

        int newLevel = member.level.level();
        return newLevel > oldLevel;
    }

    private MessageEmbed generateLevelUpMessage(ApiMemberData data)
    {
        return new GenericEmbedBuilder()
                .setColor(KatColor.LEVEL_CATEGORY)
                .setTitle("Level Up!")
                .setDescription(String.format("Congratulations on your ding, you are now **Level %d**", data.level.level()))
                .build();
    }
}
