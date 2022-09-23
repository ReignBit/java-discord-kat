package com.reign.kat.lib;

import com.reign.api.kat.models.ApiGuild;
import com.reign.api.kat.responses.PermissionGroups;
import com.reign.kat.Bot;
import com.reign.kat.lib.utils.PermissionGroupType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionHandler {
    private static final Logger log = LoggerFactory.getLogger(PermissionHandler.class);
    /**
     * Ensures the member has sufficient command permissions and discord permission to execute the command
     * in the requested channel.
     * @param member    The member to check permissions for.
     * @param channel   The channel to check permission to execute in.
     * @param discordPerms  The raw discord permission requirements.
     * @param cmdPerm   The command permission group required.
     * @return  boolean if they can execute or not.
     */
    public static boolean isPrivileged(Member member, GuildChannel channel, long discordPerms, PermissionGroupType cmdPerm)
    {
        if (Config.DEBUG_MODE && Config.DEBUG_IGNORE_PERMISSION_SYS)
        {
            log.warn("Permission system is disabled due to being in DEBUG mode.");
            log.warn("All commands will pass permission checks, set debug-ignore-permission-system to false to stop this behaviour.");
            return true;
        }

        log.trace("Checking permission for {} in channel {} with permission requirements {} {}", member.getIdLong(), channel.getIdLong(), discordPerms, cmdPerm);
        if (isOwner(channel, member)) {
            return true;
        }

        // If member has role in needed permission role AND has the correct discord perms
        long rawPermission = Permission.getRaw(member.getPermissions());
        if ((rawPermission & discordPerms) == discordPerms || discordPerms == 0)
        {
            // check snowflake permission
            List<String> snowflakes = member.getRoles().stream().map(ISnowflake::getId).collect(Collectors.toList());
            snowflakes.add(member.getId()); // Since the member's snowflake could specifically be set to a group

            return checkSnowflakePermissions(ApiGuild.get(channel.getGuild().getId()).getPermissionGroups(), snowflakes, cmdPerm);
        }
        log.trace("Permission check failed");
        return false;
    }

    static boolean checkSnowflakePermissions(PermissionGroups permissionGroups, List<String> snowflakesToCompare, PermissionGroupType requiredGroup) {
        if (requiredGroup == PermissionGroupType.EVERYONE) {
            log.trace("Permission check passed, group level EVERYONE");
            return true;
        }
        else if (requiredGroup == PermissionGroupType.MODERATOR) {
            for(String id: snowflakesToCompare)
            {
                if (permissionGroups.mod.contains(id))
                {
                    log.trace("Permission check passed, group level MODERATOR");
                    return true;
                }
            }
        }
        else if (requiredGroup == PermissionGroupType.ADMINISTRATOR) {
            for(String id: snowflakesToCompare)
            {
                if (permissionGroups.admin.contains(id))
                {
                    log.trace("Permission check passed, group level ADMINISTRATOR");
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isOwner(Guild guild, Member member)
    {
        log.trace("Permission check passed because member == owner");
        return guild.getOwnerIdLong() == (member.getIdLong());
    }

    static boolean isOwner(GuildChannel guildc, Member member)
    {
        log.trace("Permission check passed because member == owner");
        return guildc.getGuild().getOwnerIdLong() == (member.getIdLong());
    }
}
