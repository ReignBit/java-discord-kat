package com.reign.kat.lib.utils;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;

public interface IPermissionable {

    void setRequiredPermissionGroups(PermissionGroupType permisson);
    void setRequiredDiscordPermissions(int dperms);
    boolean isPrivileged(Member member, GuildChannel channel);
}
