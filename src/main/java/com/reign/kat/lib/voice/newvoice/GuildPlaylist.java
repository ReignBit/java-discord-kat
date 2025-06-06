package com.reign.kat.lib.voice.newvoice;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.handlers.GuildPlaylistResponseHandler;
import com.reign.kat.lib.utils.PreCommandResult;
import com.reign.kat.lib.voice.receive.AudioRecvManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.List;
import java.util.Objects;


public class GuildPlaylist extends AudioEventAdapter
{
    public final long guildID;

    public final GuildPlaylistResponseHandler responseHandler;
    public AudioRecvManager audioRecvManager;


    private final AudioManager jdaVoiceState;   // JDA Voice state (state in discord VC)
    private final PlaylistPlayer player;        // Our wrapper around Lava-player
    private final PlaylistQueue queue;          // Our queuing instance
    private boolean shouldPlayNextSong = true;  // Will pause the queue until true


    public GuildPlaylist(long guildID, AudioPlayerManager lavaPlayerManager)
    {
        this.guildID = guildID;

        player = new PlaylistPlayer(lavaPlayerManager, 50);
        responseHandler = new GuildPlaylistResponseHandler(guildID, player);

        player.lavaPlayer.addListener(responseHandler); // Does message to Discord
        player.lavaPlayer.addListener(this);    // We only need this for onTrackEnd in here.
        queue = new PlaylistQueue(player);


        jdaVoiceState = Objects.requireNonNull(Bot.jda.getGuildById(guildID)).getAudioManager();
        jdaVoiceState.setSendingHandler(player.getSendHandler());
    }


    /**
     * Request to play a song/playlist from any available sources
     */
    public void request(Member requester, String searchQuery)
    {
        shouldPlayNextSong = true;  // On a new request we want the queue to start as normal.
        int prevSize = queue.size();
        List<RequestedTrack> queuedTracks = queue.search(requester, searchQuery);

        if (!queuedTracks.isEmpty())
        {
            responseHandler.onRequestedTracks(queuedTracks, player);

            if (player.nowPlaying == null && prevSize == 0)
            {
                player.play(queue.dequeue());
            }
        }
        else
        {
            responseHandler.onNoMatches(searchQuery);
        }


    }

    /** Request to play a song/playlist from a specific source (using searchquery like `ytsearch:` or `spotify:` */
    public List<RequestedTrack> requestFromSource(Member requester, String searchQuery, String source)
    {
        //TODO: implement
        return null;
    }

    public boolean isPlaying() { return player.nowPlaying != null; }
    public boolean isPaused() { return player.lavaPlayer.isPaused(); }
    public RequestedTrack nowPlaying() { return player.nowPlaying; }
    public PlaylistQueue getQueue() { return queue; }
    public void move(AudioChannel channel) { jdaVoiceState.openAudioConnection(channel); }
    public void skip() { shouldPlayNextSong = true; player.stop(); }
    public void pause() { player.pause(); }
    public void resume() { player.resume(); }
    public void stop() { shouldPlayNextSong = false; player.stop(); player.nowPlaying = null; }
    public void seek(long position) { player.seek(position); }
    public MessageChannel getLastTextChannel() { return Bot.jda.getTextChannelById(responseHandler.getTextChannelID()); }

    /***
     * Export the current loaded playlist
     * @return String, list of loaded tracks
     */
    public String export() {
        StringBuilder s = new StringBuilder();

        if (nowPlaying() != null) {
            s.append(nowPlaying().url).append("\n");
        }

        queue.getQueue().forEach(track -> s.append(track.url).append("\n"));

        return s.toString();
    }

    public GuildPlaylistResponseHandler getResponseHandler() { return responseHandler; }

    public static PreCommandResult ensureTrackPlaying(Context c, CommandParameters args)
    {
        GuildPlaylist gp = GuildPlaylistPool.get(c.guild.getIdLong());
        PreCommandResult result = new PreCommandResult(false, "Nothing is playing");

        result.passed = gp.isPlaying();
        return result;
    }

    public static PreCommandResult ensureVoiceStatePreCommand(Context c, CommandParameters args)
    {
        GuildPlaylist gp = GuildPlaylistPool.get(c.guild.getIdLong());
        PreCommandResult result = new PreCommandResult(false, "");

        if (c.author.getVoiceState() != null && c.author.getVoiceState().inAudioChannel())
        {
            gp.connectIfNotConnected(c.voiceChannel);
            if (gp.isInCorrectChannel(c.author))
            {
                result.passed = true;
                result.message = "In your channel!";
            }
            else
            {
                result.message = "You need to be in the same voice channel to use this command!";
            }
            return result;
        }

        result.message = "You need to be in a voice channel to use this command!";
        return result;
    }

    public void connectIfNotConnected(AudioChannel channel)
    {
        if (!jdaVoiceState.isConnected())
        {
            jdaVoiceState.openAudioConnection(channel);
        }
    }

    /** Check if Member is in same VoiceChannel as our jdaVoiceState. Also returns true if  */
    public boolean isInCorrectChannel(Member m)
    {

        if (m.getVoiceState() != null && m.getVoiceState().inAudioChannel())
        {
            if (!jdaVoiceState.isConnected())
            {
                return true;
            }
            return Objects.requireNonNull(m.getVoiceState().getChannel()).getIdLong() == Objects.requireNonNull(jdaVoiceState.getConnectedChannel()).getIdLong();
        }
        return false;
    }

    /** Stops the player and destroys everything */
    public void destroy()
    {
        // Stop any current track
        player.stop();
        // Empty the playlist
        queue.clear();

        // Disconnect from voice
        jdaVoiceState.closeAudioConnection();

        // Destroy our Lavaplayer instance
        player.lavaPlayer.destroy();

        // Stop voice listeners
        if (audioRecvManager != null)
        {
            audioRecvManager.stopListening();
        }

    }

    @Override
    public void onTrackEnd(AudioPlayer _player, AudioTrack _track, AudioTrackEndReason endReason)
    {
        super.onTrackEnd(_player, _track, endReason);

        if (shouldPlayNextSong)
        {
            RequestedTrack nextTrack = queue.dequeue();

            player.nowPlaying = nextTrack;
            if (nextTrack != null)
            {
                player.play(nextTrack);
            }
        }
        else
        {
            player.nowPlaying = null;
        }
    }
}
