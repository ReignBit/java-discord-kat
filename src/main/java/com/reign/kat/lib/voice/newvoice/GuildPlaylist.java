package com.reign.kat.lib.voice.newvoice;

import com.reign.kat.Bot;
import com.reign.kat.lib.command.CommandParameters;
import com.reign.kat.lib.command.Context;
import com.reign.kat.lib.handlers.GuildPlaylistResponseHandler;
import com.reign.kat.lib.utils.PreCommandResult;
import com.reign.kat.lib.voice.receive.AudioRecvHandler;
import com.reign.kat.lib.voice.receive.AudioRecvManager;
import com.reign.kat.lib.voice.receive.VoiceRecognition;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;


public class GuildPlaylist extends AudioEventAdapter
{
    private static final Logger log = LoggerFactory.getLogger(GuildPlaylist.class);

    public final long guildID;

    public final GuildPlaylistResponseHandler responseHandler;
    public final AudioRecvManager audioRecvManager;


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
        queue = new PlaylistQueue();


        jdaVoiceState = Objects.requireNonNull(Bot.jda.getGuildById(guildID)).getAudioManager();
        jdaVoiceState.setSendingHandler(player.getSendHandler());

        audioRecvManager = new AudioRecvManager(this);
        audioRecvManager.addListener(VoiceRecognition.instance());

        jdaVoiceState.setReceivingHandler(audioRecvManager.handler);
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
    public RequestedTrack nowPlaying() { return player.nowPlaying; }
    public List<RequestedTrack> getQueue() { return queue.getQueue(); }
    public void move(AudioChannel channel) { jdaVoiceState.openAudioConnection(channel); }
    public void skip() { shouldPlayNextSong = true; player.stop(); }
    public void pause() { player.pause(); }
    public void resume() { player.resume(); }
    public void stop() { shouldPlayNextSong = false; player.stop(); player.nowPlaying = null; }
    public void seek(long position) { player.seek(position); }



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
    }

    @Override
    public void onTrackEnd(AudioPlayer _player, AudioTrack _track, AudioTrackEndReason endReason)
    {
        super.onTrackEnd(_player, _track, endReason);

        if (queue.size() > 0 && shouldPlayNextSong)
        {
            RequestedTrack nextTrack = queue.dequeue();
            player.nowPlaying = nextTrack;
            player.play(nextTrack);
            log.debug("GuildPlaylist.onTrackEnd#trueif np == null: " + (nowPlaying() == null));
        }
        else
        {
            player.nowPlaying = null;
            log.debug("GuildPlaylist.onTrackEnd#falseif np == null: " + (nowPlaying() == null));

        }

        log.debug("Track ended, now playing: " + player.nowPlaying);
    }
}
