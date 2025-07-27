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
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class BirthdayAddCommand extends Command {

    public BirthdayAddCommand(Category category) {
        super(new String[]{"add"}, "add", "Add/Change a birthday", category);
        /*
            !birthday config <tag> <value>
         */
        //TODO: Add a Converter which converts from a list of valid inputs
        addConverter(new MemberConverter("member", "Member", null));
        addConverter(new StringConverter("birthday", "Can be in format: [DD/MM/YYYY]", null));

    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());

        Member member = args.get("member");
        String strTimestamp = args.get("birthday");

        DateTimeFormatter fm;
        try {
            fm = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        } catch (IllegalArgumentException exception) {
            ctx.send(new ExceptionEmbed().setDescription("Invalid Date!\nRequired format: `dd/MM/yyyy`").build());
            return;
        }
        LocalDate date = LocalDate.parse(strTimestamp, fm);
        LocalDateTime dateTime = date.atStartOfDay();
        Timestamp ts = Timestamp.valueOf(dateTime);

        HashMap<String, Long> currentBirthdays = category.datastore.getField(BirthdayCategory.DK_MEMBERS, guild);
        currentBirthdays.put(member.getId(), ts.getTime());

        category.datastore.updateField(BirthdayCategory.DK_MEMBERS, currentBirthdays, guild);

        ctx.send(new GenericEmbedBuilder()
                .setTitle("Birthday Updated!")
                .build());
    }
}
