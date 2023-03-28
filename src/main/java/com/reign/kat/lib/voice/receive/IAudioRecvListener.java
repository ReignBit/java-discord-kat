package com.reign.kat.lib.voice.receive;

import net.dv8tion.jda.api.entities.Member;

import java.util.Queue;

public interface IAudioRecvListener
{
    void onUserFinishedSpeaking(Member member, AudioUser data);
}
