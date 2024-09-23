package com.reign.kat.commands.voice;

import com.reign.kat.lib.command.Command;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.converters.GreedyStringConverter;
import com.reign.kat.lib.converters.VideoSourceGreedyConverter;
import com.reign.kat.lib.voice.newvoice.GuildPlaylist;
import com.reign.kat.lib.voice.newvoice.GuildPlaylistPool;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ExportPlaylistCommand extends Command {
    private static final Logger log = LoggerFactory.getLogger(ExportPlaylistCommand.class);

    public ExportPlaylistCommand() {
        super(new String[]{"export", "e"}, "export", "Exports the current loaded tracks to a file.");
        addConverter(new GreedyStringConverter(
                "filename",
                "Name of a song or URL",
                DateTimeFormatter.BASIC_ISO_DATE.format(LocalDateTime.now())
        ));
        setShowTyping(true);
    }

    @Override
    public void execute(Context ctx, CommandParameters args) throws Exception {
        GuildPlaylist playlist = GuildPlaylistPool.get(ctx.guild.getIdLong());
        playlist.getResponseHandler().setTextChannelID(ctx.channel().getIdLong());


        if (ctx.canProvideInteractionHook())
            playlist.getResponseHandler().setHook(ctx.hook());

        String filename = args.get("filename") + ".kat";
        String export = playlist.export();
        InputStream stream = new ByteArrayInputStream(export.getBytes(StandardCharsets.UTF_8));

        ctx.channel().sendFiles(FileUpload.fromData(stream, filename)).queue();
        ctx.send(String.format("Exported (%d) tracks to `%s`", playlist.getQueue().size(), filename));
    }
}
