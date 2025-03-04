package com.reign.kat.commands.helpful;

import com.reign.api.kat.models.ApiGuild;
import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.data.DatastoreField;
import com.reign.kat.lib.converters.GreedyStringConverter;
import com.reign.kat.lib.converters.MemberConverter;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.Map;

public class TagCommand extends Command {


    public TagCommand() {
        super(new String[]{"t"}, "t", "Show the tag in chat.");

        addConverter(new GreedyStringConverter("tagName", "Name of the tag", null));

        setDatastore()
                .addField("tag.tags", new DatastoreField<>(new HashMap<String, String>()));
    }

    @Override
    public void execute(Context ctx, CommandParameters params) throws Exception {
        ApiGuild guild = ApiGuild.get(ctx.guild.getId());

        Map<String, String> tags = datastore.getField("tag.tags", guild);

        String tagText = tags.get(params.get("tagName").toString());
        ctx.send(tagText);
    }
}
