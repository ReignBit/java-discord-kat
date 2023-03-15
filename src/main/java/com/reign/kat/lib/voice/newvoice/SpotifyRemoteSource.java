package com.reign.kat.lib.voice.newvoice;

import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifyConfig;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifySourceManager;
import com.reign.kat.lib.Config;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;

public class SpotifyRemoteSource
{
    public static AudioSourceManager build(AudioPlayerManager manager)
    {
        SpotifyConfig spotifyConfig = new SpotifyConfig();
        spotifyConfig.setClientId(Config.VOICE_SPOTIFY_CLIENT_ID);
        spotifyConfig.setClientSecret(Config.VOICE_SPOTIFY_CLIENT_SECRET);
        spotifyConfig.setCountryCode(Config.VOICE_SPOTIFY_COUNTRY_CODE);

        return new SpotifySourceManager(null, spotifyConfig, manager);
    }
}
