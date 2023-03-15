package com.reign.kat.lib.voice;

import com.reign.kat.Bot;
import com.reign.kat.lib.utils.Utilities;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;

import java.util.Objects;

public class RequestedTrack {
    public final String guildId;
    public final String userId;
    public final AudioTrack track;


    public RequestedTrack(String guildId, String userId, AudioTrack track)
    {
        this.guildId = guildId;
        this.userId = userId;
        this.track = track;
    }

    public RequestedTrack(Member member, AudioTrack track)
    {
        this.guildId = member.getGuild().getId();
        this.userId = member.getId();
        this.track = track;
    }

    public Member getRequester()
    {
        return Objects.requireNonNull(Bot.jda.getGuildById(guildId)).getMemberById(userId);
    }

    public String getTrackTitle() { return track.getInfo().title; }

    public String getDurationTimestamp() { return Utilities.timeConversion(track.getDuration()); }

    public AudioTrack getTrack()
    {
        return track;
    }


}
