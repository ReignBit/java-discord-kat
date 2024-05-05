package com.reign.kat.lib.voice.music;

import com.reign.kat.Bot;
import com.reign.kat.lib.messages.VoiceMessages;
import dev.arbjerg.lavalink.client.AbstractAudioLoadResultHandler;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.*;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackResultHandler extends AbstractAudioLoadResultHandler
{
    private static final Logger log = LoggerFactory.getLogger(TrackResultHandler.class);

    private final long channelId;
    private final Member requester;
    private final String query;
    private final Link link;

    public TrackResultHandler(long channelId, Member requester, String query, Link link)
    {
        this.channelId = channelId;
        this.requester = requester;
        this.query = query;
        this.link = link;
    }


    @Override
    public void loadFailed(@NotNull LoadFailed loadFailed)
    {
        log.warn("Failed to load track");
        Bot.jda.getTextChannelById(channelId).sendMessageEmbeds(VoiceMessages.loadFailed(loadFailed)).queue();
    }

    @Override
    public void noMatches()
    {
        log.warn("No matches found for query");
        Bot.jda.getTextChannelById(channelId).sendMessageEmbeds(VoiceMessages.noMatches(query)).queue();

    }

    @Override
    public void onPlaylistLoaded(@NotNull PlaylistLoaded playlistLoaded)
    {
        log.debug("Playlist loaded");
    }

    @Override
    public void onSearchResultLoaded(@NotNull SearchResult searchResult)
    {
        log.debug("Search results found");
    }

    @Override
    public void ontrackLoaded(@NotNull TrackLoaded trackLoaded)
    {
        log.debug("Track loaded");
        final Track track = trackLoaded.getTrack();

        final var requesterData = new RequesterData(requester.getIdLong());
        link.createOrUpdatePlayer()
                .


    }

    record RequesterData(long id) {}
}
