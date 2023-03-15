package com.reign.kat.lib.voice.newvoice;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Responsible for loading/searching for tracks and playlists for the GuildPlaylist
 */
public class PlaylistTrackLoader
{
    private static final Logger log = LoggerFactory.getLogger(PlaylistTrackLoader.class);

    public PlaylistQueue queue;

    public PlaylistTrackLoader(PlaylistQueue queue)
    {
        this.queue = queue;
    }

    public List<AudioTrack> search(String query)
    {
        ArrayList<AudioTrack> loadedTracks = new ArrayList<>();

        try
        {
            GuildPlaylistPool.playerManager.loadItemOrdered(
                    this,
                    query,
                    new AudioLoadResultHandler() {

                        @Override
                        public void trackLoaded(AudioTrack track)
                        {
                            loadedTracks.add(track);
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist playlist)
                        {
                            log.debug("PlaylistTrackLoader.playlistLoaded");
                            AudioTrack firstTrack = playlist.getSelectedTrack();

                            if (firstTrack == null) { firstTrack = playlist.getTracks().get(0); }

                            if (playlist.isSearchResult())
                            {
                                loadedTracks.add(firstTrack);
                            }
                            else
                            {
                                loadedTracks.addAll(playlist.getTracks());
                            }
                        }

                        @Override
                        public void noMatches()
                        {
                            log.debug("No matches found.");
                        }

                        @Override
                        public void loadFailed(FriendlyException exception)
                        {
                            log.error("failed to load", exception);
                        }
                    }
            ).get();
        } catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
        log.debug("PlaylistTrackLoader.search -> loadedTracks = {}", loadedTracks);
        return loadedTracks;
    }
}
