package com.reign.kat.commands.birthday;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.category.Category;
import com.reign.kat.lib.converters.MemberConverter;
import com.reign.kat.lib.converters.StringConverter;
import com.reign.kat.lib.embeds.ExceptionEmbed;
import com.reign.kat.lib.embeds.GenericEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class BirthdayRemoveCommand extends Command {

    public BirthdayRemoveCommand(Category category) {
        super(new String[]{"remove"}, "remove", "Remove a birthday", category);
        /*
            !birthday config <tag> <value>
         */
        //TODO: Add a Converter which converts from a list of valid inputs
        addConverter(new MemberConverter("member", "Member", null));

    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());

        Member member = args.get("member");

        HashMap<String, Long> currentBirthdays = category.datastore.getField(BirthdayCategory.DK_MEMBERS, guild);
        currentBirthdays.remove(member.getId());

        category.datastore.updateField(BirthdayCategory.DK_MEMBERS, currentBirthdays, guild);

        ctx.send(new GenericEmbedBuilder()
                .setTitle("Birthday Removed!")
                .build());
    }
}
