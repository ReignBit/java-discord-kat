package com.reign.kat.commands.birthday;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.Bot;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.command.data.DatastoreField;
import com.reign.kat.lib.embeds.BirthdayEmbed;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class BirthdayCategory extends Category {
    private static final Logger log = LoggerFactory.getLogger(BirthdayCategory.class);
    public static final String DK_FEATURE_ENABLED = "birthday.enabled";
    public static final String DK_ANNOUNCE_ID = "birthday.announce_id";
    public static final String DK_MEMBERS = "birthday.members";

    public BirthdayCategory() {
        setHelpMenuEmoji(":birthday:");
        setDatastore()
                .addField(DK_FEATURE_ENABLED, new DatastoreField<>(false))
                .addField(DK_ANNOUNCE_ID, new DatastoreField<>(""))
                .addField(DK_MEMBERS, new DatastoreField<>(new HashMap<String, Long>()));

        registerCommand(new BirthdayCommand(this));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();

        Duration duration = Duration.between(now, midnight);

        log.info("Birthday event will start in {} minutes", duration.toMinutes());
        ScheduledFuture<?> birthdayEvent = Bot.executorService.scheduleAtFixedRate(
                this::onBirthdayCheck,
                duration.toMinutes(),
                24 * 60,
                TimeUnit.MINUTES);

    }

    private boolean isBirthdayDate(Timestamp timestamp) {
        LocalDate localDate = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        return localDate.getMonth() == today.getMonth() && localDate.getDayOfMonth() == today.getDayOfMonth();
    }

    private void sendBirthdayAnnouncement(Member member, Guild guild, String channel_id) {
        TextChannel channel = guild.getTextChannelById(channel_id);
        if (channel != null) {
            channel.sendMessage("@everyone")
                    .setEmbeds(new BirthdayEmbed(member).build())
                    .setAllowedMentions(Collections.singleton(Message.MentionType.EVERYONE))
                    .queue();
        }
    }

    public void onBirthdayCheck() {
        log.debug("onBirthdayCheck");
        for (Guild guild : Bot.jda.getGuilds()) {
            ApiGuild apiGuild = ApiGuild.get(guild.getId());
            boolean allowed = datastore.getField(DK_FEATURE_ENABLED, apiGuild);
            if (!allowed) {
                // Do not process guilds which do not have birthday notifs enabled.
                continue;
            }

            HashMap<String, Long> members = datastore.getField(DK_MEMBERS, apiGuild);
            for (Map.Entry<String, Long> data : members.entrySet()) {
                Member member = guild.getMemberById(data.getKey());

                String channel_id = datastore.getField(DK_ANNOUNCE_ID, apiGuild);
                if (member != null && !channel_id.isEmpty()) {
                    if (isBirthdayDate(new Timestamp(data.getValue()))) {
                        // Birthday!!!
                        log.debug("Birthday {}", member.getId());
                        sendBirthdayAnnouncement(member, guild, channel_id);
                    }
                }
            }

        }
    }
}
